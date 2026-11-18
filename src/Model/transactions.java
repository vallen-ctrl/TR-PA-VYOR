/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.transactionsInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

/**
 *
 * @author ricor
 */



/**
 * Model untuk tabel `transactions`.
 * Nama class = nama tabel (model.java mengambil nama tabel dari nama class).
 */
public class transactions extends model {

    private static final String[] STATUS = {"selesai", "dibatalkan", "refund"};

    /**
     * Menyimpan satu transaksi.
     * @return id transaksi yang baru dibuat, atau -1 kalau gagal.
     */
    public long add(transactionsInterface trx) {
        // Kolom wajib
        List<String> kolom = new ArrayList<>(List.of(
                "sub_total", "diskon_voucher", "pajak_persen", "pajak",
                "grand_total", "bayar", "kembalian", "status"));
        List<String> nilai = new ArrayList<>(List.of(
                trx.sub_total.toPlainString(), trx.diskon_voucher.toPlainString(),
                trx.pajak_persen.toPlainString(), trx.pajak.toPlainString(),
                trx.grand_total.toPlainString(), trx.bayar.toPlainString(),
                trx.kembalian.toPlainString(), trx.status));

        // Kolom opsional: hanya dimasukkan kalau ada isinya
        if (trx.customer_id != null) {
            kolom.add("customer_id");
            nilai.add(trx.customer_id.toString());
        }
        if (trx.voucher_id != null) {
            kolom.add("voucher_id");
            nilai.add(trx.voucher_id.toString());
        }
        if (trx.kode != null) {
            kolom.add("kode");
            nilai.add(trx.kode);
        }

        try {
            int hasil = this.insert(
                    kolom.toArray(new String[0]),
                    nilai.toArray(new String[0])
            ).execute();
            // execute() tidak melempar error SQL, tetapi mengembalikan -1
            if (hasil < 0) return -1;

            // LAST_INSERT_ID bersifat per koneksi, dan objek ini punya koneksi sendiri
            try (ResultSet rs = this.setQuery("SELECT LAST_INSERT_ID()").run()) {
                if (rs != null && rs.next()) return rs.getLong(1);
            }
        } catch (SQLException ex) {
            System.getLogger(transactions.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return -1;
    }

    /**
     * Daftar transaksi untuk layar riwayat. Semua filter boleh null (tidak dipakai).
     *
     * @param status     selesai / dibatalkan / refund
     * @param dari       tanggal awal (termasuk)
     * @param sampai     tanggal akhir (termasuk)
     * @param customerId filter member
     * @param limit      jumlah baris per halaman
     * @param offset     jumlah baris yang dilompati (halaman - 1) * limit
     */
    public List<transactionsInterface> daftar(String status, LocalDate dari, LocalDate sampai,
            Integer customerId, int limit, int offset) {
        return daftar(status, dari, sampai, customerId, null, limit, offset);
    }

    /**
     * Sama seperti di atas, ditambah filter voucher:
     * true = hanya transaksi yang memakai voucher, false = hanya yang tanpa voucher, null = semua.
     */
    public List<transactionsInterface> daftar(String status, LocalDate dari, LocalDate sampai,
            Integer customerId, Boolean denganVoucher, int limit, int offset) {
        List<transactionsInterface> hasil = new ArrayList<>();

        // model.java hanya memasang LIMIT dan JOIN kalau ada WHERE,
        // jadi kita selalu mulai dengan "1 = 1". Urutan penting: ORDER BY paling akhir.
        this.select().where("1 = 1");
        if (status != null) {
            this.where("AND status = '" + cekStatus(status) + "'");
        }
        if (dari != null) {
            this.where("AND DATE(waktu) >= '" + dari + "'");
        }
        if (sampai != null) {
            this.where("AND DATE(waktu) <= '" + sampai + "'");
        }
        if (customerId != null) {
            this.where("AND customer_id = " + customerId.intValue());
        }
        if (denganVoucher != null) {
            this.where(denganVoucher ? "AND voucher_id IS NOT NULL" : "AND voucher_id IS NULL");
        }
        this.where("ORDER BY waktu DESC");
        this.setLimit(limit).setOffset(offset);

        try (ResultSet rs = this.run()) {
            if (rs == null) return hasil;
            while (rs.next()) {
                hasil.add(dariBaris(rs));
            }
        } catch (SQLException ex) {
            System.getLogger(transactions.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return hasil;
    }

    /** @return transaksi dengan id tersebut, atau null kalau tidak ada. */
    public transactionsInterface cariById(long id) {
        try (ResultSet rs = this.select().where("id = " + id).run()) {
            if (rs != null && rs.next()) return dariBaris(rs);
        } catch (SQLException ex) {
            System.getLogger(transactions.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return null;
    }

    // ---------- bantuan ----------

    private String cekStatus(String status) {
        for (String s : STATUS) {
            if (s.equals(status)) return s;
        }
        throw new InputMismatchException("tidak menerima status [diterima: selesai, dibatalkan, refund] -> " + status);
    }

    private transactionsInterface dariBaris(ResultSet rs) throws SQLException {
        Timestamp waktu = rs.getTimestamp("waktu");
        return new transactionsInterface(
                rs.getLong("id"),
                rs.getString("kode"),
                waktu != null ? waktu.toLocalDateTime() : null,
                intNullable(rs, "customer_id"),
                intNullable(rs, "voucher_id"),
                rs.getBigDecimal("sub_total"),
                rs.getBigDecimal("diskon_voucher"),
                rs.getBigDecimal("pajak_persen"),
                rs.getBigDecimal("pajak"),
                rs.getBigDecimal("grand_total"),
                rs.getBigDecimal("bayar"),
                rs.getBigDecimal("kembalian"),
                rs.getString("status"));
    }

    private Integer intNullable(ResultSet rs, String kolom) throws SQLException {
        int nilai = rs.getInt(kolom);
        return rs.wasNull() ? null : nilai;
    }
}