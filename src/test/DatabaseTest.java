/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import config.Database;
import java.util.Scanner;

/**
 *
 * @author vallen
 */
public class DatabaseTest {

    private static final Scanner in = new Scanner(System.in);
    private static final Database database = new Database();

    public static void main(String[] args) {
        int pilihan;
        do {
            tampilMenu();
            pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1 ->
                    tesKoneksi();
                case 0 ->
                    System.out.println("Keluar dari program.");
                default ->
                    System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void tampilMenu() {
        System.out.println("\n===== MENU DATABASE =====");
        System.out.println("1. Tes koneksi database");
        System.out.println("0. Keluar");
        System.out.println("=========================");
    }

    // START
    private static void tesKoneksi() {
        try {
            database.start();
            System.out.println("Koneksi database berhasil.");
            database.close();
        } catch (Exception e) {
            System.out.println("Gagal koneksi database: " + e.getMessage());
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