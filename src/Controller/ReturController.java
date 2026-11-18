/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import config.Database;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ricor
 */

public class ReturController {

    public static final int BATAS_HARI = 3;

    /** Satu item yang diretur. layakDijual = kondisi barang baik (stok bertambah kembali). */
    public record ItemRetur(long transactionItemId, int jumlah, boolean layakDijual) {
    }

    public record HasilRetur(boolean berhasil, String pesan, long returId,
            BigDecimal totalRefund, String statusTransaksi, boolean voucherDikembalikan) {
    }

    private record ItemAsal(int productId, int jumlah, BigDecimal harga) {
    }

    /** Pelanggaran aturan retur (bukan error teknis). */
    private static class AturanRetur extends RuntimeException {

        AturanRetur(String pesan) {
            super(pesan);
        }
    }

    public HasilRetur prosesRetur(long transaksiId, String alasan, List<ItemRetur> daftarItem) {
        if (alasan == null || alasan.isBlank()) {
            return gagal("Alasan retur wajib diisi.");
        }
        if (alasan.trim().length() > 255) {
            return gagal("Alasan retur maksimal 255 karakter.");
        }
        if (daftarItem == null || daftarItem.isEmpty()) {
            return gagal("Pilih minimal satu item untuk diretur.");
        }

        Database db = new Database();
        boolean terbuka = false;
        Connection c = null;
        try {
            Statement stm = db.start();
            terbuka = true;
            c = stm.getConnection();
            c.setAutoCommit(false);

            HasilRetur hasil = jalankan(c, transaksiId, alasan.trim(), daftarItem);
            c.commit();
            return hasil;
        } catch (AturanRetur e) {
            batalkan(c);
            return gagal(e.getMessage());
        } catch (Exception e) {
            // Error teknis (database, koneksi, dan sebagainya): semuanya dibatalkan
            batalkan(c);
            return gagal("Refund gagal diproses, tidak ada data yang berubah. Penyebab: " + e.getMessage());
        } finally {
            if (terbuka) {
                try {
                    db.close();
                } catch (SQLException ignored) {
                    // koneksi sudah selesai dipakai
                }
            }
        }
    }

    private HasilRetur jalankan(Connection c, long trxId, String alasan, List<ItemRetur> permintaan)
            throws SQLException {

        // 1. Baca dan kunci baris transaksi supaya dua retur tidak berjalan bersamaan
        Timestamp waktu = null;
        Integer voucherId = null;
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;
        String status = null;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT waktu, voucher_id, sub_total, grand_total, status FROM transactions WHERE id = ? FOR UPDATE")) {
            ps.setLong(1, trxId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new AturanRetur("Transaksi #" + trxId + " tidak ditemukan.");
                }
                waktu = rs.getTimestamp("waktu");
                int v = rs.getInt("voucher_id");
                voucherId = rs.wasNull() ? null : v;
                subTotal = rs.getBigDecimal("sub_total");
                grandTotal = rs.getBigDecimal("grand_total");
                status = rs.getString("status");
            }
        }

        // 2. Aturan status dan batas waktu
        if ("dibatalkan".equals(status)) {
            throw new AturanRetur("Transaksi #" + trxId + " sudah dibatalkan, tidak bisa diretur.");
        }
        LocalDateTime batas = waktu.toLocalDateTime().plusDays(BATAS_HARI);
        if (LocalDateTime.now().isAfter(batas)) {
            throw new AturanRetur("Batas retur " + BATAS_HARI + " hari sudah lewat (batas: "
                    + batas.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ").");
        }
        if (subTotal.signum() <= 0) {
            throw new AturanRetur("Sub total transaksi #" + trxId + " tidak valid, refund tidak bisa dihitung.");
        }

        // 3. Item asli transaksi dan jumlah yang sudah pernah diretur
        Map<Long, ItemAsal> asal = new HashMap<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id, product_id, jumlah, harga FROM transaction_items WHERE transaction_id = ?")) {
            ps.setLong(1, trxId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    asal.put(rs.getLong("id"), new ItemAsal(
                            rs.getInt("product_id"), rs.getInt("jumlah"), rs.getBigDecimal("harga")));
                }
            }
        }
        Map<Long, Integer> sudahRetur = new HashMap<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT ri.transaction_item_id AS item_id, SUM(ri.jumlah) AS total "
                + "FROM return_items ri JOIN `returns` r ON r.id = ri.return_id "
                + "WHERE r.transaction_id = ? GROUP BY ri.transaction_item_id")) {
            ps.setLong(1, trxId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sudahRetur.put(rs.getLong("item_id"), rs.getInt("total"));
                }
            }
        }

        // 4. Validasi permintaan dan hitung nilai barang yang diretur
        Set<Long> dipilih = new HashSet<>();
        Map<Long, Integer> jumlahBaru = new HashMap<>();
        BigDecimal nilaiBarang = BigDecimal.ZERO;
        for (ItemRetur r : permintaan) {
            ItemAsal item = asal.get(r.transactionItemId());
            if (item == null) {
                throw new AturanRetur("Item #" + r.transactionItemId()
                        + " bukan bagian dari transaksi #" + trxId + ".");
            }
            if (!dipilih.add(r.transactionItemId())) {
                throw new AturanRetur("Item #" + r.transactionItemId() + " dipilih lebih dari sekali.");
            }
            int sisa = item.jumlah() - sudahRetur.getOrDefault(r.transactionItemId(), 0);
            if (sisa <= 0) {
                throw new AturanRetur("Item #" + r.transactionItemId() + " sudah diretur seluruhnya.");
            }
            if (r.jumlah() <= 0 || r.jumlah() > sisa) {
                throw new AturanRetur("Jumlah retur item #" + r.transactionItemId()
                        + " harus 1 sampai " + sisa + ".");
            }
            jumlahBaru.put(r.transactionItemId(), r.jumlah());
            nilaiBarang = nilaiBarang.add(item.harga().multiply(BigDecimal.valueOf(r.jumlah())));
        }

        // 5. Refund proporsional terhadap yang benar-benar dibayar (termasuk pajak dan diskon)
        BigDecimal refund = nilaiBarang.multiply(grandTotal).divide(subTotal, 2, RoundingMode.HALF_UP);

        // 6. Simpan header retur
        long returId;
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO `returns` (transaction_id, alasan, total_refund) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, trxId);
            ps.setString(2, alasan);
            ps.setBigDecimal(3, refund);
            ps.executeUpdate();
            try (ResultSet kunci = ps.getGeneratedKeys()) {
                if (!kunci.next()) {
                    throw new SQLException("id retur tidak terbaca setelah disimpan");
                }
                returId = kunci.getLong(1);
            }
        }

        // 7. Simpan item retur, dan kembalikan stok untuk barang yang kondisinya baik
        try (PreparedStatement insItem = c.prepareStatement(
                "INSERT INTO return_items (return_id, transaction_item_id, jumlah) VALUES (?, ?, ?)");
                PreparedStatement stok = c.prepareStatement(
                        "UPDATE products SET stok = stok + ? WHERE id = ?")) {
            for (ItemRetur r : permintaan) {
                insItem.setLong(1, returId);
                insItem.setLong(2, r.transactionItemId());
                insItem.setInt(3, r.jumlah());
                insItem.executeUpdate();

                if (r.layakDijual()) {
                    stok.setInt(1, r.jumlah());
                    stok.setInt(2, asal.get(r.transactionItemId()).productId());
                    stok.executeUpdate();
                }
            }
        }

        // 8. Apakah seluruh barang pada transaksi ini sudah kembali?
        boolean semuaKembali = true;
        for (Map.Entry<Long, ItemAsal> e : asal.entrySet()) {
            int kembali = sudahRetur.getOrDefault(e.getKey(), 0) + jumlahBaru.getOrDefault(e.getKey(), 0);
            if (kembali < e.getValue().jumlah()) {
                semuaKembali = false;
                break;
            }
        }
        String statusBaru = semuaKembali ? "dibatalkan" : "refund";
        try (PreparedStatement ps = c.prepareStatement("UPDATE transactions SET status = ? WHERE id = ?")) {
            ps.setString(1, statusBaru);
            ps.setLong(2, trxId);
            ps.executeUpdate();
        }

        // 9. Voucher kembali aktif hanya kalau seluruh transaksi diretur dan voucher belum kedaluwarsa
        boolean voucherKembali = false;
        if (semuaKembali && voucherId != null) {
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE vouchers SET status = 'aktif' "
                    + "WHERE id = ? AND status = 'dipakai' AND berlaku_sampai >= CURDATE()")) {
                ps.setInt(1, voucherId);
                voucherKembali = ps.executeUpdate() > 0;
            }
        }

        String pesan = "Retur #" + returId + " tersimpan. Total refund: Rp. " + refund.toPlainString()
                + ". Status transaksi: " + statusBaru + "."
                + (voucherKembali ? " Voucher dikembalikan." : "");
        return new HasilRetur(true, pesan, returId, refund, statusBaru, voucherKembali);
    }

    private HasilRetur gagal(String pesan) {
        return new HasilRetur(false, pesan, -1, BigDecimal.ZERO, null, false);
    }

    private void batalkan(Connection c) {
        if (c == null) {
            return;
        }
        try {
            c.rollback();
        } catch (SQLException ignored) {
            // koneksi akan ditutup oleh pemanggil
        }
    }
}
