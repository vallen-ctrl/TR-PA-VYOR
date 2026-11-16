/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import Model.interfaces.transaction_itemInterface;
import Model.interfaces.transactionsInterface;
import Model.transaction_items;
import Model.transactions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author ricor
 */


/**
 * Uji coba model transaksi lewat console (tanpa GUI).
 * Catatan: product_id (dan customer_id kalau diisi) harus sudah ada di database.
 */


public class transaction {

    private static final Scanner in = new Scanner(System.in);
    private static final transactions trx = new transactions();
    private static final transaction_items items = new transaction_items();

    public static void main(String[] args) {
        int pilihan;
        do {
            tampilMenu();
            pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1 ->
                    tambah();
                case 2 ->
                    daftar();
                case 3 ->
                    detail();
                case 0 ->
                    System.out.println("Keluar dari program.");
                default ->
                    System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void tampilMenu() {
        System.out.println("\n===== MENU TRANSAKSI =====");
        System.out.println("1. Tambah transaksi contoh");
        System.out.println("2. Lihat daftar transaksi");
        System.out.println("3. Lihat detail transaksi");
        System.out.println("0. Keluar");
        System.out.println("==========================");
    }

    // CREATE
    private static void tambah() {
        try {
            String cust = bacaText("Customer ID (kosongkan jika umum): ");
            Integer customerId = cust.isEmpty() ? null : Integer.valueOf(cust);

            // Kumpulkan item dulu, supaya sub_total bisa dihitung
            List<String[]> daftarItem = new ArrayList<>();
            BigDecimal subTotal = BigDecimal.ZERO;
            String lagi;
            do {
                String[] it = {
                    bacaText("Product ID : "),
                    bacaText("Kode       : "),
                    bacaText("Nama       : "),
                    bacaText("Jumlah     : "),
                    bacaText("Satuan     : "),
                    bacaText("Harga      : ")
                };
                daftarItem.add(it);
                subTotal = subTotal.add(new BigDecimal(it[5]).multiply(new BigDecimal(it[3])));
                lagi = bacaText("Tambah item lagi? (y/n): ");
            } while (lagi.equalsIgnoreCase("y"));

            BigDecimal diskon = BigDecimal.ZERO; // voucher belum dipakai di test ini
            BigDecimal pajakPersen = new BigDecimal("11.00");
            BigDecimal pajak = subTotal.subtract(diskon).multiply(pajakPersen)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal grandTotal = subTotal.subtract(diskon).add(pajak);
            System.out.println("Grand total: " + grandTotal.toPlainString());

            BigDecimal bayar = new BigDecimal(bacaText("Bayar: "));
            if (bayar.compareTo(grandTotal) < 0) {
                System.out.println("Pembayaran kurang, transaksi dibatalkan.");
                return;
            }
            BigDecimal kembalian = bayar.subtract(grandTotal);

            long id = trx.add(new transactionsInterface(customerId, null,
                    subTotal, diskon, pajakPersen, pajak, grandTotal, bayar, kembalian, "selesai"));
            if (id < 0) {
                System.out.println("Gagal menyimpan transaksi.");
                return;
            }
            for (String[] it : daftarItem) {
                items.add(new transaction_itemInterface(id, Integer.valueOf(it[0]),
                        it[1], it[2], Integer.valueOf(it[3]), it[4], new BigDecimal(it[5])));
            }
            System.out.println("Transaksi tersimpan dengan id " + id + ". Kembalian: " + kembalian.toPlainString());
        } catch (Exception e) {
            System.out.println("Gagal menambah transaksi: " + e.getMessage());
        }
    }

    // READ (daftar)
    private static void daftar() {
        List<transactionsInterface> hasil = trx.daftar(null, null, null, null, 10, 0);
        if (hasil.isEmpty()) {
            System.out.println("Belum ada transaksi.");
            return;
        }
        for (transactionsInterface t : hasil) {
            System.out.println("#" + t.id + " | " + t.waktu + " | customer: " + t.customer_id
                    + " | total: " + t.grand_total.toPlainString() + " | " + t.status);
        }
    }

    // READ (detail)
    private static void detail() {
        int id = bacaInt("ID transaksi: ");
        transactionsInterface t = trx.cariById(id);
        if (t == null) {
            System.out.println("Transaksi tidak ditemukan.");
            return;
        }
        System.out.println("Transaksi #" + t.id + " | " + t.waktu + " | " + t.status);
        for (transaction_itemInterface it : items.byTransaksi(id)) {
            System.out.println("  " + it.nama + " x" + it.jumlah + " " + it.satuan
                    + " @ " + it.harga.toPlainString() + " = " + it.total.toPlainString());
        }
        System.out.println("Sub total : " + t.sub_total.toPlainString());
        System.out.println("Diskon    : " + t.diskon_voucher.toPlainString());
        System.out.println("Pajak     : " + t.pajak.toPlainString());
        System.out.println("Grand total: " + t.grand_total.toPlainString());
        System.out.println("Bayar     : " + t.bayar.toPlainString() + " | Kembalian: " + t.kembalian.toPlainString());
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