/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.returnsInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ricor
 */

/**
 * Model untuk tabel `returns`. Nama class = nama tabel.
 * Aturan bisnis retur (batas 3 hari, stok, voucher, status transaksi)
 * dikerjakan nanti di minggu 3; ini baru akses datanya.
 */

public class returns extends model {

    /**
     * Menyimpan satu header retur.
     * @return id retur yang baru dibuat, atau -1 kalau gagal.
     */
    public long add(returnsInterface retur) {
        try {
            int hasil = this.insert(
                    new String[]{"transaction_id", "alasan", "total_refund"},
                    new String[]{retur.transaction_id.toString(), retur.alasan,
                        retur.total_refund.toPlainString()}
            ).execute();
            if (hasil < 0) return -1;

            // LAST_INSERT_ID bersifat per koneksi, dan objek ini punya koneksi sendiri
            try (ResultSet rs = this.setQuery("SELECT LAST_INSERT_ID()").run()) {
                if (rs != null && rs.next()) return rs.getLong(1);
            }
        } catch (SQLException ex) {
            System.getLogger(returns.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return -1;
    }

    /** Semua retur milik satu transaksi, yang terbaru di atas. */
    public List<returnsInterface> byTransaksi(long transactionId) {
        List<returnsInterface> hasil = new ArrayList<>();
        try (ResultSet rs = this.select()
                .where("transaction_id = " + transactionId + " ORDER BY waktu DESC").run()) {
            if (rs == null) return hasil;
            while (rs.next()) {
                hasil.add(dariBaris(rs));
            }
        } catch (SQLException ex) {
            System.getLogger(returns.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return hasil;
    }

    /** @return retur dengan id tersebut, atau null kalau tidak ada. */
    public returnsInterface cariById(long id) {
        try (ResultSet rs = this.select().where("id = " + id).run()) {
            if (rs != null && rs.next()) return dariBaris(rs);
        } catch (SQLException ex) {
            System.getLogger(returns.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return null;
    }

    private returnsInterface dariBaris(ResultSet rs) throws SQLException {
        Timestamp waktu = rs.getTimestamp("waktu");
        return new returnsInterface(
                rs.getLong("id"),
                rs.getLong("transaction_id"),
                waktu != null ? waktu.toLocalDateTime() : null,
                rs.getString("alasan"),
                rs.getBigDecimal("total_refund"));
    }
}
