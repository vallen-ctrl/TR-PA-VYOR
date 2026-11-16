/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.transaction_itemInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ricor
 */


/**
 * Model untuk tabel `transaction_items`.
 * Nama class harus sama dengan nama tabel karena model.java memakai nama class sebagai nama tabel.
 */
public class transaction_items extends model {

    /** Menyimpan satu item. @return jumlah baris yang tersimpan, -1 kalau gagal. */
    public int add(transaction_itemInterface item) {
        try {
            return this.insert(
                    new String[]{"transaction_id", "product_id", "kode", "nama",
                        "jumlah", "satuan", "harga", "total"},
                    new String[]{item.transaction_id.toString(), item.product_id.toString(),
                        item.kode, item.nama, item.jumlah.toString(), item.satuan,
                        item.harga.toPlainString(), item.total.toPlainString()}
            ).execute();
        } catch (SQLException ex) {
            System.getLogger(transaction_items.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return -1;
    }

    /** Semua item milik satu transaksi (untuk layar detail dan cetak struk). */
    public List<transaction_itemInterface> byTransaksi(long transactionId) {
        List<transaction_itemInterface> hasil = new ArrayList<>();
        try (ResultSet rs = this.select().where("transaction_id = " + transactionId).run()) {
            if (rs == null) return hasil;
            while (rs.next()) {
                hasil.add(new transaction_itemInterface(
                        rs.getLong("id"),
                        rs.getLong("transaction_id"),
                        rs.getInt("product_id"),
                        rs.getString("kode"),
                        rs.getString("nama"),
                        rs.getInt("jumlah"),
                        rs.getString("satuan"),
                        rs.getBigDecimal("harga")));
            }
        } catch (SQLException ex) {
            System.getLogger(transaction_items.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return hasil;
    }
}