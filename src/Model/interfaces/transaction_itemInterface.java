/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.math.BigDecimal;
/**
 *
 * @author ricor
 */


/**
 * Data satu baris tabel `transaction_items`.
 * Kolom `total` tidak dihitung otomatis oleh database,
 * jadi dihitung di konstruktor (harga x jumlah).
 */
public class transaction_itemInterface {
    public Long id;
    public Long transaction_id;
    public Integer product_id;
    public String kode;
    public String nama;
    public Integer jumlah;
    public String satuan;
    public BigDecimal harga;
    public BigDecimal total;

    // Untuk insert
    public transaction_itemInterface(Long transaction_id, Integer product_id,
            String kode, String nama, Integer jumlah, String satuan, BigDecimal harga) {
        if (jumlah == null || jumlah <= 0) {
            throw new IllegalArgumentException("jumlah harus lebih dari 0");
        }
        this.transaction_id = transaction_id;
        this.product_id = product_id;
        this.kode = kode;
        this.nama = nama;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.harga = harga;
        this.total = harga.multiply(BigDecimal.valueOf(jumlah));
    }

    // Untuk data hasil baca dari database
    public transaction_itemInterface(Long id, Long transaction_id, Integer product_id,
            String kode, String nama, Integer jumlah, String satuan, BigDecimal harga) {
        this(transaction_id, product_id, kode, nama, jumlah, satuan, harga);
        this.id = id;
    }
}