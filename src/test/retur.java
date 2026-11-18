/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import Controller.ReturController;
import Controller.ReturController.HasilRetur;
import Controller.ReturController.ItemRetur;
import Model.interfaces.return_itemInterface;
import Model.interfaces.returnsInterface;
import Model.interfaces.transaction_itemInterface;
import Model.interfaces.transactionsInterface;
import Model.return_items;
import Model.returns;
import Model.transaction_items;
import Model.transactions;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author ricor
 */

public class retur {

    private static final Scanner in = new Scanner(System.in);
    private static final ReturController kontrol = new ReturController();
    private static final returns returModel = new returns();
    private static final return_items returItemModel = new return_items();
    private static final transactions trxModel = new transactions();
    private static final transaction_items trxItemModel = new transaction_items();

    public static void main(String[] args) {
        int pilihan;
        do {
            tampilMenu();
            pilihan = (int) bacaLong("Pilih menu: ");
            switch (pilihan) {
                case 1 ->
                    proses();
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
        System.out.println("\n===== MENU RETUR =====");
        System.out.println("1. Proses retur");
        System.out.println("2. Lihat retur per transaksi");
        System.out.println("3. Lihat detail retur");
        System.out.println("0. Keluar");
        System.out.println("======================");
    }

    // PROSES RETUR (semua aturan dijalankan oleh ReturController)
    private static void proses() {
        long trxId = bacaLong("ID transaksi: ");
        transactionsInterface t = trxModel.cariById(trxId);
        if (t == null) {
            System.out.println("Transaksi tidak ditemukan.");
            return;
        }
        System.out.println("Transaksi #" + t.id + " | " + t.waktu + " | status: " + t.status);
        System.out.println("Item pada transaksi:");
        for (transaction_itemInterface it : trxItemModel.byTransaksi(trxId)) {
            System.out.println("  [id item " + it.id + "] " + it.nama + " x" + it.jumlah
                    + " @ " + it.harga.toPlainString());
        }

        String alasan = bacaText("Alasan retur: ");

        List<ItemRetur> daftarItem = new ArrayList<>();
        String lagi;
        do {
            long itemId = bacaLong("ID item yang diretur: ");
            int jumlah = (int) bacaLong("Jumlah retur: ");
            String kondisi = bacaText("Kondisi barang (baik/rusak): ");
            if (kondisi.equalsIgnoreCase("baik") || kondisi.equalsIgnoreCase("rusak")) {
                daftarItem.add(new ItemRetur(itemId, jumlah, kondisi.equalsIgnoreCase("baik")));
            } else {
                System.out.println("Kondisi harus 'baik' atau 'rusak', item ini dilewati.");
            }
            lagi = bacaText("Tambah item retur lagi? (y/n): ");
        } while (lagi.equalsIgnoreCase("y"));

        HasilRetur hasil = kontrol.prosesRetur(trxId, alasan, daftarItem);
        System.out.println((hasil.berhasil() ? "BERHASIL: " : "GAGAL: ") + hasil.pesan());
    }

    // READ (daftar per transaksi)
    private static void daftar() {
        long trxId = bacaLong("ID transaksi: ");
        List<returnsInterface> hasil = returModel.byTransaksi(trxId);
        if (hasil.isEmpty()) {
            System.out.println("Belum ada retur untuk transaksi ini.");
            return;
        }
        for (returnsInterface r : hasil) {
            System.out.println("#" + r.id + " | " + r.waktu + " | refund: "
                    + r.total_refund.toPlainString() + " | " + r.alasan);
        }
    }

    // READ (detail)
    private static void detail() {
        long id = bacaLong("ID retur: ");
        returnsInterface r = returModel.cariById(id);
        if (r == null) {
            System.out.println("Retur tidak ditemukan.");
            return;
        }
        System.out.println("Retur #" + r.id + " | transaksi #" + r.transaction_id + " | " + r.waktu);
        System.out.println("Alasan: " + r.alasan);
        for (return_itemInterface it : returItemModel.byRetur(id)) {
            System.out.println("  item transaksi #" + it.transaction_item_id + " x" + it.jumlah);
        }
        System.out.println("Total refund: " + r.total_refund.toPlainString());
        transactionsInterface t = trxModel.cariById(r.transaction_id);
        if (t != null) {
            System.out.println("Status transaksi sekarang: " + t.status);
        }
    }

    private static String bacaText(String label) {
        System.out.print(label);
        return in.nextLine().trim();
    }

    private static long bacaLong(String label) {
        try {
            return Long.parseLong(bacaText(label));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
