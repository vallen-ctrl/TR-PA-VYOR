/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.util.List;
import Model.products;
import Model.interfaces.productInterface;
import Model.interfaces.categoriesInterface;
import Model.categories;
import java.sql.SQLException;
import Model.product_batches;
import Model.interfaces.product_batchInterface;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author vallen
 */
public class ProdukController {

    private products prod = new products();
    private categories kategori = new categories();
    private product_batches batch = new product_batches();

    public List<productInterface> get() throws SQLException {
        return prod.getAll();
    }

    public int AddProduct(productInterface insertProduk) throws SQLException {
        return this.prod.add(insertProduk);
    }

    public List<categoriesInterface> getCategories() throws SQLException {
        return this.kategori.get();
    }
    
    public void main(String[] args) throws SQLException {
        for(categoriesInterface c : this.getCategories()){
            System.out.println("C -> " + c.id);
        }
    }

    public void tambahStokProduk(String nomor, String nama, Integer categoryId, String satuan,
            String hargaJual, int supplierId, String hargaBeli, String qtyText,
            LocalDate tanggalPesan, LocalDate tanggalMasuk, LocalDate tanggalKadaluarsa)
            throws SQLException {

        int qty = Integer.parseInt(qtyText.trim());
        BigDecimal hargaBeliBD = new BigDecimal(hargaBeli.trim());

        productInterface produk = prod.getProductByNumber(nomor);

        // Produk belum ada -> insert produk baru dengan stok 0
        if (produk == null) {
            productInterface baru = new productInterface(
                    nomor, nama, categoryId, supplierId, satuan,
                    hargaBeliBD.doubleValue(), Double.parseDouble(hargaJual.trim()),
                    0, tanggalMasuk, false
            );
            prod.add(baru);
            produk = prod.getProductByNumber(nomor); // ambil ulang untuk dapat id
        }

        // Insert batch (qty_sisa = qty karena batch baru)
        product_batchInterface dataBatch = new product_batchInterface(
                produk.id, supplierId, hargaBeliBD, qty, qty,
                tanggalPesan, tanggalMasuk, tanggalKadaluarsa
        );
        this.batch.addbatch(dataBatch);

        // Update stok: stok sekarang + qty, dan tanggal terakhir disuplai
        prod.tambahStok(produk.id, produk.stok, qty, tanggalMasuk);
    }
}
