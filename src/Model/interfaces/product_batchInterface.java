/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author vallen
 */
public class product_batchInterface{
    public Integer id;
    public Integer supplier_id;
    public Integer product_id;
    public BigDecimal harga_beli;
    public Integer qty;
    public Integer qty_sisa;
    public LocalDate tanggal_pemesanan;
    public LocalDate tanggal_masuk;
    public LocalDate tanggal_kadaluarsa;
    public boolean ditangani;
    public String supplierName;
    public String alamatSupplier;

    public product_batchInterface(Integer id, Integer supplier_id, Integer product_id, BigDecimal harga_beli, Integer qty, Integer qty_sisa, LocalDate tanggal_pemesanan, LocalDate tanggal_masuk, LocalDate tanggal_kadaluarsa, boolean ditangani, String supplierName, String alamatSupplier) {
        this.id = id;
        this.supplier_id = supplier_id;
        this.product_id = product_id;
        this.harga_beli = harga_beli;
        this.qty = qty;
        this.qty_sisa = qty_sisa;
        this.tanggal_pemesanan = tanggal_pemesanan;
        this.tanggal_masuk = tanggal_masuk;
        this.tanggal_kadaluarsa = tanggal_kadaluarsa;
        this.ditangani = ditangani;
        this.supplierName = supplierName;
        this.alamatSupplier = alamatSupplier;
    }

    

    public product_batchInterface(int supplier_id, int product_id, BigDecimal harga_beli, int qty, int qty_sisa, LocalDate tanggal_pemesanan, LocalDate tanggal_masuk, LocalDate tanggal_kadaluarsa) {
        this.supplier_id = supplier_id;
        this.product_id = product_id;
        this.harga_beli = harga_beli;
        this.qty = qty;
        this.qty_sisa = qty_sisa;
        this.tanggal_pemesanan = tanggal_pemesanan;
        this.tanggal_masuk = tanggal_masuk;
        this.tanggal_kadaluarsa = tanggal_kadaluarsa;
    }
    
    

    public product_batchInterface(int id, int supplier_id, int product_id, BigDecimal harga_beli, int qty, int qty_sisa, LocalDate tanggal_pemesanan, LocalDate tanggal_masuk, LocalDate tanggal_kadaluarsa, boolean ditangani) {
        this.id = id;
        this.supplier_id = supplier_id;
        this.product_id = product_id;
        this.harga_beli = harga_beli;
        this.qty = qty;
        this.qty_sisa = qty_sisa;
        this.tanggal_pemesanan = tanggal_pemesanan;
        this.tanggal_masuk = tanggal_masuk;
        this.tanggal_kadaluarsa = tanggal_kadaluarsa;
        this.ditangani = ditangani;
    }
    
    
}
