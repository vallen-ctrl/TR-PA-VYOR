/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import Model.interfaces.SupplierInterface;
import Model.suppliers;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author vallen
 */
public class supplier {
    
    private static final Scanner in = new Scanner(System.in);
    private static final suppliers supplier = new suppliers();

    public static void main(String[] args) {
        int pilihan;
        do {
            tampilMenu();
            pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1 -> tambah();
                case 2 -> lihat();
                case 3 -> ubah();
                case 4 -> hapus();
                case 0 -> System.out.println("Keluar dari program.");
                default -> System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void tampilMenu() {
        System.out.println("\n===== MENU SUPPLIER =====");
        System.out.println("1. Tambah supplier");
        System.out.println("2. Lihat supplier");
        System.out.println("3. Ubah supplier");
        System.out.println("4. Hapus supplier");
        System.out.println("0. Keluar");
        System.out.println("=========================");
    }

    // CREATE
    private static void tambah() {
        try {
            String nama = bacaText("Nama     : ");
            String alamat = bacaText("Alamat   : ");
            String noHp = bacaText("Nomor HP : ");

            // id = 0 dan dihapus = 0 hanya pengisi, tidak dipakai saat insert
            supplier.add(new SupplierInterface(0, nama, alamat, noHp, 0));
            System.out.println("Supplier berhasil ditambahkan.");
        } catch (Exception e) {
            System.out.println("Gagal menambah supplier: " + e.getMessage());
        }
    }

    // READ
    private static void lihat() {
        try {
            int limit = 10;
            int halaman = Math.max(1, bacaInt("Halaman (mulai 1): "));
            int offset = (halaman - 1) * limit;

            List<SupplierInterface> data = supplier.get(limit, offset);
            if (data.isEmpty()) {
                System.out.println("Data kosong.");
                return;
            }

            System.out.printf("%-4s %-25s %-35s %-14s%n", "ID", "Nama", "Alamat", "No HP");
            for (SupplierInterface s : data) {
                System.out.printf("%-4d %-25s %-35s %-14s%n", s.id, s.nama, s.alamat, s.nomorHp);
            }
        } catch (Exception e) {
            System.out.println("Gagal menampilkan supplier: " + e.getMessage());
        }
    }

    // UPDATE
    private static void ubah() {
        try {
            int id = bacaInt("ID supplier yang diubah: ");
            String nama = bacaText("Nama baru     : ");
            String alamat = bacaText("Alamat baru   : ");
            String noHp = bacaText("Nomor HP baru : ");

            int hasil = supplier.update(
                    new String[]{"nama", "alamat", "nomor_hp"},
                    new String[]{nama, alamat, noHp}).where("id = " + id).execute();

            System.out.println(hasil > 0 ? "Data berhasil diubah." : "Data tidak ditemukan / gagal diubah.");
        } catch (Exception e) {
            System.out.println("Gagal mengubah supplier: " + e.getMessage());
        }
    }

    // DELETE (soft delete: dihapus = 1)
    private static void hapus() {
        try {
            int id = bacaInt("ID supplier yang dihapus: ");
            int hasil = supplier.removeSupplier(id);
            System.out.println(hasil > 0 ? "Data berhasil dihapus." : "Data tidak ditemukan / gagal dihapus.");
        } catch (Exception e) {
            System.out.println("Gagal menghapus supplier: " + e.getMessage());
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
}
