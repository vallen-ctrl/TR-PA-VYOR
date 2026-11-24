/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import Model.categories;
import Model.interfaces.categoriesInterface;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author vallen
 */
public class category {

    private static final Scanner in = new Scanner(System.in);
    private static final categories category = new categories();

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
        System.out.println("\n===== MENU CATEGORY =====");
        System.out.println("1. Tambah category");
        System.out.println("2. Hapus category");
        System.out.println("3. Lihat category");
        System.out.println("0. Keluar");
        System.out.println("=========================");
    }

    // CREATE
    private static void tambah() {
        try {
            String nama = bacaText("Nama            : ");
            boolean dihapus = bacaBool("Dihapus (y/n)   : ");

            int hasil = category.add(new categoriesInterface(nama, dihapus));
            System.out.println(hasil > 0 ? "Data berhasil ditambahkan." : "Data tidak ditemukan / gagal ditambahkan.");
        } catch (Exception e) {
            System.out.println("Gagal menambah category: " + e.getMessage());
        }
    }

    // DELETE
    private static void hapus() {
        try {
            int id = bacaInt("ID category yang dihapus: ");
            int hasil = category.delete(id);
            System.out.println(hasil > 0 ? "Data berhasil dihapus." : "Data tidak ditemukan / gagal dihapus.");
        } catch (Exception e) {
            System.out.println("Gagal menghapus category: " + e.getMessage());
        }
    }

    // READ
    private static void lihat() {
        try {
            int limit = 10;
            int halaman = Math.max(1, bacaInt("Halaman (mulai 1): "));
            int offset = (halaman - 1) * limit;

            List<categoriesInterface> data = category.get();
            if (data.isEmpty()) {
                System.out.println("Data kosong.");
                return;
            }

            System.out.printf("%-25s %-8s%n", "Nama", "Dihapus");
            for (categoriesInterface c : data) {
                System.out.printf("%-25s %-8s%n", c.name, c.dihapus);
            }
        } catch (Exception e) {
            System.out.println("Gagal melihat category: " + e.getMessage());
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

    private static boolean bacaBool(String label) {
        return bacaText(label).equalsIgnoreCase("y");
    }
}