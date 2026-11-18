/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

/**
 *
 * @author ricor
 */

/**
 * Data satu baris tabel `return_items` (barang yang diretur).
 * transaction_item_id merujuk ke baris di `transaction_items`.
 */

public class return_itemInterface {
    public Long id;
    public Long return_id;
    public Long transaction_item_id;
    public Integer jumlah;

    // Untuk insert
    public return_itemInterface(Long return_id, Long transaction_item_id, Integer jumlah) {
        if (jumlah == null || jumlah <= 0) {
            throw new IllegalArgumentException("jumlah retur harus lebih dari 0");
        }
        this.return_id = return_id;
        this.transaction_item_id = transaction_item_id;
        this.jumlah = jumlah;
    }

    // Untuk data hasil baca dari database
    public return_itemInterface(Long id, Long return_id, Long transaction_item_id, Integer jumlah) {
        this(return_id, transaction_item_id, jumlah);
        this.id = id;
    }
}