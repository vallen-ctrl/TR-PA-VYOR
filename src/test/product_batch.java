/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import Model.interfaces.product_batchInterface;
import Model.product_batches;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author vallen
 */
public class product_batch {

    private static final Scanner in = new Scanner(System.in);
    private static final product_batches product_batch = new product_batches();

    public static void main(String[] args) {
        int pilihan;
        do {
            tampilMenu();
            pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1 ->
                    tambah();
                case 2 ->
                    hapus();
                case 3 ->
                    setDitangani();
                case 4 ->
                    lihat();
                case 0 ->
                    System.out.println("Keluar dari program.");
                default ->
                    System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void tampilMenu() {
        System.out.println("\n===== MENU PRODUCT BATCH =====");
        System.out.println("1. Tambah batch");
        System.out.println("2. Hapus batch");
        System.out.println("3. Ubah status ditangani batch");
        System.out.println("4. Lihat batch");
        System.out.println("0. Keluar");
        System.out.println("==============================");
    }

    // CREATE
    private static void tambah() {
        try {
            int supplierId = bacaInt("Supplier ID    : ");
            int productId = bacaInt("Product ID     : ");
            double hargaBeli = bacaDouble("Harga beli     : ");
            int qty = bacaInt("Qty            : ");
            int qtySisa = bacaInt("Qty sisa       : ");
            LocalDate tglPemesanan = LocalDate.parse(bacaText("Tgl pemesanan (yyyy-MM-dd)   : "));
            LocalDate tglMasuk = LocalDate.parse(bacaText("Tgl masuk (yyyy-MM-dd)       : "));
            LocalDate tglKadaluarsa = LocalDate.parse(bacaText("Tgl kadaluarsa (yyyy-MM-dd)  : "));

            product_batch.addbatch(new product_batchInterface(
                    supplierId, productId, hargaBeli, qty, qtySisa,
                    tglPemesanan, tglMasuk, tglKadaluarsa));
            System.out.println("Batch berhasil ditambahkan.");
        } catch (Exception e) {
            System.out.println("Gagal menambah batch: " + e.getMessage());
        }
    }

    // DELETE
    private static void hapus() {
        try {
            String id = bacaText("ID batch yang dihapus: ");
            int hasil = product_batch.deletebatch(id);
            System.out.println(hasil > 0 ? "Data berhasil dihapus." : "Data tidak ditemukan / gagal dihapus.");
        } catch (Exception e) {
            System.out.println("Gagal menghapus batch: " + e.getMessage());
        }
    }

    // SETDITANGANI
    private static void setDitangani() {
        try {
            int batchId = bacaInt("ID batch          : ");
            boolean ditangani = bacaBool("Ditangani (y/n)   : ");
            int hasil = product_batch.setDitangani(batchId, ditangani);
            System.out.println(hasil > 0 ? "Data berhasil diubah." : "Data tidak ditemukan / gagal diubah.");
        } catch (Exception e) {
            System.out.println("Gagal mengubah status batch: " + e.getMessage());
        }
    }

    // READ
    private static void lihat() {
        try {
            int limit = 10;
            int halaman = Math.max(1, bacaInt("Halaman (mulai 1): "));
            int offset = (halaman - 1) * limit;

            List<product_batchInterface> data = product_batch.get(limit, offset);
            if (data.isEmpty()) {
                System.out.println("Data kosong.");
                return;
            }

            System.out.printf("%-4s %-9s %-11s %-12s %-6s %-9s %-12s %-12s %-12s %-10s%n",
                    "ID", "Supplier", "Product", "Harga Beli", "Qty", "Qty Sisa",
                    "Pemesanan", "Masuk", "Kadaluarsa", "Ditangani");
            for (product_batchInterface b : data) {
                System.out.printf("%-4d %-9d %-11d %-12.2f %-6d %-9d %-12s %-12s %-12s %-10s%n",
                        b.id, b.supplier_id, b.product_id, b.harga_beli, b.qty, b.qty_sisa,
                        b.tanggal_pemesanan, b.tanggal_masuk, b.tanggal_kadaluarsa, b.ditangani);
            }
        } catch (Exception e) {
            System.out.println("Gagal melihat batch: " + e.getMessage());
        }
    }

    private static String bacaText(String label) {
        System.out.print(label);
        return in.nextLine().trim();
    }

    private static int bacaInt(String label) {
        try {
            return Integer.parseInt(bacaText(label));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double bacaDouble(String label) {
        try {
            return Double.parseDouble(bacaText(label));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static boolean bacaBool(String label) {
        return bacaText(label).equalsIgnoreCase("y");
    }
}