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

    public int updateProduk(String produkIdTemp, productInterface produk) throws SQLException {
        String[] colums = {"nomor", "nama", "category_id", "harga_jual"};
        String[] values = {String.valueOf(produk.nomor), produk.nama, String.valueOf(produk.category_id), produk.harga_jual.toString()};
 
        return this.prod.update(colums, values)
                .where("nomor = "+ this.prod.esc(produkIdTemp))
                .execute();
    }

    public int HapusProduk(String nomorProduk) throws SQLException {
        productInterface produk = prod.getProductByNumber(nomorProduk);
        return this.prod.remove(produk.id);
    }

    public int AddProduct(productInterface insertProduk) throws SQLException {
        return this.prod.add(insertProduk);
    }

    public List<categoriesInterface> getCategories() throws SQLException {
        return this.kategori.get();
    }

    public void main(String[] args) throws SQLException {
        this.tambahStokProduk(
                "567", // nomor
                "Kopi Arabika 250g", // nama
                1, // categoryId
                "pcs", // satuan
                "25000", // hargaJual
                2, // supplierId
                "18000", // hargaBeli
                "10", // qtyText
                LocalDate.now(), // tanggalPesan
                LocalDate.now(), // tanggalMasuk
                LocalDate.now().plusMonths(6) // tanggalKadaluarsa
        );

        System.out.println("Stok produk berhasil ditambahkan.");

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

        System.out.println("id -> " + produk.id);

//         Insert batch (qty_sisa = qty karena batch baru)
        product_batchInterface dataBatch = new product_batchInterface(
                supplierId, produk.id, hargaBeliBD, qty, qty,
                tanggalPesan, tanggalMasuk, tanggalKadaluarsa
        );
        this.batch.addbatch(dataBatch);

        // Update stok: stok sekarang + qty, dan tanggal terakhir disuplai
        prod.tambahStok(produk.id, produk.stok, qty, tanggalMasuk);
    }
}
