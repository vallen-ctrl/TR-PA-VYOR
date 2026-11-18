/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.return_itemInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ricor
 */

/**
 * Model untuk tabel `return_items`. Nama class = nama tabel.
 */

public class return_items extends model {

    /** Menyimpan satu item retur. @return jumlah baris yang tersimpan, -1 kalau gagal. */
    public int add(return_itemInterface item) {
        try {
            return this.insert(
                    new String[]{"return_id", "transaction_item_id", "jumlah"},
                    new String[]{item.return_id.toString(), item.transaction_item_id.toString(),
                        item.jumlah.toString()}
            ).execute();
        } catch (SQLException ex) {
            System.getLogger(return_items.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return -1;
    }

    /** Semua item milik satu retur. */
    public List<return_itemInterface> byRetur(long returnId) {
        List<return_itemInterface> hasil = new ArrayList<>();
        try (ResultSet rs = this.select().where("return_id = " + returnId).run()) {
            if (rs == null) return hasil;
            while (rs.next()) {
                hasil.add(new return_itemInterface(
                        rs.getLong("id"),
                        rs.getLong("return_id"),
                        rs.getLong("transaction_item_id"),
                        rs.getInt("jumlah")));
            }
        } catch (SQLException ex) {
            System.getLogger(return_items.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return hasil;
    }
}
