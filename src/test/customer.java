/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import Model.customers;
import Model.interfaces.customerInterface;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author vallen
 */
public class customer {

    private static final Scanner in = new Scanner(System.in);
    private static final customers customer = new customers();

    public static void main(String[] args) {
        int pilihan;
        do {
            tampilMenu();
            pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1 ->
                    tambah();
                case 2 ->
                    lihat();
                case 3 ->
                    ubah();
                case 4 ->
                    hapus();
                case 0 ->
                    System.out.println("Keluar dari program.");
                default ->
                    System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void tampilMenu() {
        System.out.println("\n===== MENU CUSTOMER =====");
        System.out.println("1. Tambah customer");
        System.out.println("2. Lihat customer");
        System.out.println("3. Ubah customer");
        System.out.println("4. Hapus customer");
        System.out.println("0. Keluar");
        System.out.println("=========================");
    }

    // CREATE
    private static void tambah() {
        try {
            String kode = bacaText("Kode           : ");
            String nama = bacaText("Nama           : ");
            LocalDate tglLahir = LocalDate.parse(bacaText("Tgl lahir (yyyy-MM-dd): "));
            String noHp = bacaText("Nomor HP       : ");

            customer.add(new customerInterface(kode, nama, tglLahir, noHp));
            System.out.println("Customer berhasil ditambahkan.");
        } catch (Exception e) {
            System.out.println("Gagal menambah customer: " + e.getMessage());
        }
    }

    // READ
    private static void lihat() {
        int limit = 10;
        int halaman = Math.max(1, bacaInt("Halaman (mulai 1): "));
        int offset = (halaman - 1) * limit;

        List<customerInterface> data = customer.getCustomer(limit, offset);
        if (data.isEmpty()) {
            System.out.println("Data kosong.");
            return;
        }

        System.out.printf("%-4s %-10s %-25s %-12s %-14s %-12s %-6s%n",
                "ID", "Kode", "Nama", "Tgl Lahir", "No HP", "Tgl Daftar", "Hapus");
        for (customerInterface c : data) {
            System.out.printf("%-4d %-10s %-25s %-12s %-14s %-12s %-6d%n",
                    c.id, c.kode, c.nama, c.tanggal_lahir, c.nomorHp, c.tanggal_daftar, c.dihapus);
        }
    }

    // UPDATE
    private static void ubah() {
        try {
            String kode = bacaText("Kode customer yang diubah: ");
            String nama = bacaText("Nama baru                 : ");
            String noHp = bacaText("Nomor HP baru             : ");
//            LocalDate tglLahir = LocalDate.parse(bacaText("Tgl lahir baru (yyyy-MM-dd): "));


            int hasil = customer.updateCustomer(
                    new String[]{"nama", "nomor_hp"},
                    new String[]{nama, noHp},
                    "kode = '" + kode + "'");

            System.out.println(hasil > 0 ? "Data berhasil diubah." : "Data tidak ditemukan / gagal diubah.");
        } catch (Exception e) {
            System.out.println("Gagal mengubah customer: " + e.getMessage());
        }
    }

    // DELETE (soft delete: dihapus = 1)
    private static void hapus() {
        String kode = bacaText("Kode customer yang dihapus: ");
        // Kutip ditambahkan di sini karena removeCustomer() memakai "kode = %s"
        int hasil = customer.removeCustomer("'" + kode + "'");
        System.out.println(hasil > 0 ? "Data berhasil dihapus." : "Data tidak ditemukan / gagal dihapus.");
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
