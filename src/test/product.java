/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import Model.interfaces.productInterface;
import Model.products;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author vallen
 */
public class product {

    private static final Scanner in = new Scanner(System.in);
    private static final products product = new products();

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
                    lihat();
                case 0 ->
                    System.out.println("Keluar dari program.");
                default ->
                    System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void tampilMenu() {
        System.out.println("\n===== MENU PRODUCT =====");
        System.out.println("1. Tambah product");
        System.out.println("2. Hapus product");
        System.out.println("3. Lihat product");
        System.out.println("0. Keluar");
        System.out.println("========================");
    }

    // CREATE
    private static void tambah() {
        try {
            String nomor = bacaText("Nomor (maks 13)  : ");
            String nama = bacaText("Nama             : ");
            int categoryId = bacaInt("Category ID      : ");
            int supplierId = bacaInt("Supplier ID      : ");
            String satuan = bacaText("Satuan           : ");
            double hargaBeli = bacaDouble("Harga beli       : ");
            double hargaJual = bacaDouble("Harga jual       : ");
            int stok = bacaInt("Stok             : ");
            LocalDate terakhirDisuplai = LocalDate.parse(bacaText("Terakhir disuplai (yyyy-MM-dd) : "));
            boolean dihapus = bacaBool("Dihapus (y/n)    : ");

            // TODO: sesuaikan urutan parameter konstruktor productInterface untuk data baru
            int hasil = product.add(new productInterface(
                    nomor, nama, categoryId, supplierId, satuan,
                    hargaBeli, hargaJual, stok, terakhirDisuplai, dihapus));
            System.out.println(hasil > 0 ? "Data berhasil ditambahkan." : "Data tidak ditemukan / gagal ditambahkan.");
        } catch (Exception e) {
            System.out.println("Gagal menambah product: " + e.getMessage());
        }
    }

    // DELETE
    private static void hapus() {
        try {
            int id = bacaInt("ID product yang dihapus: ");
            int hasil = product.remove(id);
            System.out.println(hasil > 0 ? "Data berhasil dihapus." : "Data tidak ditemukan / gagal dihapus.");
        } catch (Exception e) {
            System.out.println("Gagal menghapus product: " + e.getMessage());
        }
    }

    // READ
    private static void lihat() {
        try {
            int limit = 10;
            int halaman = Math.max(1, bacaInt("Halaman (mulai 1): "));
            int offset = (halaman - 1) * limit;

            List<productInterface> data = product.getAll();
            if (data.isEmpty()) {
                System.out.println("Data kosong.");
                return;
            }

            System.out.printf("%-4s %-14s %-20s %-9s %-9s %-8s %-11s %-11s %-6s %-12s %-8s%n",
                    "ID", "Nomor", "Nama", "Category", "Supplier", "Satuan",
                    "Harga Beli", "Harga Jual", "Stok", "Disuplai", "Dihapus");
            for (productInterface p : data) {
                System.out.printf("%-4d %-14s %-20s %-9s %-9s %-8s %-11.2f %-11.2f %-6d %-12s %-8s%n",
                        p.id, p.nomor, p.nama, p.String_categories, p.String_supplier, p.satuan,
                        p.harga_beli, p.harga_jual, p.stok, p.terakhir_disuppli, p.dihapus);
            }
        } catch (Exception e) {
            System.out.println("Gagal melihat product: " + e.getMessage());
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