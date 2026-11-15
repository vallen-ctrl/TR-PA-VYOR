/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import Model.interfaces.vouchersInterface;
import Model.vouchers;
import java.time.LocalDate;
import java.util.Scanner;

/**
 *
 * @author vallen
 */
public class voucher {

    private static final Scanner in = new Scanner(System.in);
    private static final vouchers voucher = new vouchers();

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
                    updateStatus();
                case 0 ->
                    System.out.println("Keluar dari program.");
                default ->
                    System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void tampilMenu() {
        System.out.println("\n===== MENU VOUCHER =====");
        System.out.println("1. Tambah voucher");
        System.out.println("2. Hapus voucher");
        System.out.println("3. Ubah status voucher");
        System.out.println("0. Keluar");
        System.out.println("========================");
    }

    // CREATE
    private static void tambah() {
        try {
            int customerId = bacaInt("Customer ID    : ");
            String kode = bacaText("Kode           : ");
            String jenis = bacaText("Jenis          : ");
            LocalDate berlakuDari = LocalDate.parse(bacaText("Berlaku dari (yyyy-MM-dd)   : "));
            LocalDate berlakuSampai = LocalDate.parse(bacaText("Berlaku sampai (yyyy-MM-dd) : "));
            String status = bacaText("Status         : ");
            float diskon = bacafloat("Diskon         :");

            // TODO: sesuaikan urutan parameter konstruktor vouchersInterface
            voucher.add(new vouchersInterface(customerId, kode, jenis, diskon, berlakuDari, berlakuSampai, status));
            System.out.println("Voucher berhasil ditambahkan.");
        } catch (Exception e) {
            System.out.println("Gagal menambah voucher: " + e.getMessage());
        }
    }

    // DELETE
    private static void hapus() {
        String kode = bacaText("Kode voucher yang dihapus: ");
        int hasil = voucher.remove("'" + kode + "'");
        System.out.println(hasil > 0 ? "Data berhasil dihapus." : "Data tidak ditemukan / gagal dihapus.");
    }

    // UPDATE STATUS
    private static void updateStatus() {
        int status = bacaInt("Status (0 = aktif, 1 = dipakai, 2 = kadaluarsa): ");
        try {
            int hasil = voucher.updateStatus(status);
            System.out.println(hasil > 0 ? "Data berhasil diubah." : "Data tidak ditemukan / gagal diubah.");
        } catch (Exception e) {
            System.out.println("Gagal mengubah status voucher: " + e.getMessage());
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
    
    private static float bacafloat(String label) {
        try {
            return Float.parseFloat(bacaText(label));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}