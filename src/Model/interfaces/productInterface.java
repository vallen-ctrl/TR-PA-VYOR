/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author vallen
 */
public class productInterface {
    public Integer id;
    public String nomor;
    public String nama;
    public Integer category_id;
    public Integer supplier_id;
    public String satuan;
    public Double harga_beli;
    public Double harga_jual;
    public Integer stok;
    public LocalDate terakhir_disuppli;
    public boolean dihapus;
    public String String_categories;    
    public String String_supplier;

    public productInterface(Integer id, String nomor, String nama, Integer category_id, Integer supplier_id, String satuan, Double harga_beli, Double harga_jual, Integer stok, LocalDate terakhir_disuppli, boolean dihapus, String String_categories, String String_supplier) {
        this.id = id;
        this.nomor = nomor;
        this.nama = nama;
        this.category_id = category_id;
        this.supplier_id = supplier_id;
        this.satuan = satuan;
        this.harga_beli = harga_beli;
        this.harga_jual = harga_jual;
        this.stok = stok;
        this.terakhir_disuppli = terakhir_disuppli;
        this.dihapus = dihapus;
        this.String_categories = String_categories;
        this.String_supplier = String_supplier;
    }
    
    public productInterface(String nomor, String nama, Integer category_id, Integer supplier_id, String satuan, Double harga_beli, Double harga_jual, Integer stok, LocalDate terakhir_disuppli, boolean dihapus) {
        this.nomor = nomor;
        this.nama = nama;
        this.category_id = category_id;
        this.supplier_id = supplier_id;
        this.satuan = satuan;
        this.harga_beli = harga_beli;
        this.harga_jual = harga_jual;
        this.stok = stok;
        this.terakhir_disuppli = terakhir_disuppli;
        this.dihapus = dihapus;
    }
   
    public productInterface(Integer id, String nomor, String nama, Integer category_id, Integer supplier_id, String satuan, Double harga_beli, Double harga_jual, Integer stok, LocalDate terakhir_disuppli, boolean dihapus) {
        this.id = id;
        this.nomor = nomor;
        this.nama = nama;
        this.category_id = category_id;
        this.supplier_id = supplier_id;
        this.satuan = satuan;
        this.harga_beli = harga_beli;
        this.harga_jual = harga_jual;
        this.stok = stok;
        this.terakhir_disuppli = terakhir_disuppli;
        this.dihapus = dihapus;
    }
    
    
}
