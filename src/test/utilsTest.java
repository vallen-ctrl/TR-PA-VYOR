/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;
import utils.GenerateRandomKode;
import utils.LuhnAlgorithm;
import utils.voucher;

/**
 *
 * @author vallen
 */
public class utilsTest {
      private static final Scanner in = new Scanner(System.in);

    private static final GenerateRandomKode randomKode = new GenerateRandomKode();
    private static final LuhnAlgorithm luhn = new LuhnAlgorithm();
    private static final voucher voucher = new voucher();

    public static void main(String[] args) {

        int pilihan;

        do {
            tampilMenu();
            pilihan = bacaInt("Pilih menu: ");

            switch (pilihan) {
                case 1 -> testGenerateRandomKode();
                case 2 -> testLuhnGenerate();
                case 3 -> testLuhnValid();
                case 4 -> testCheckValidDate();
                case 5 -> testGetVoucher();
                case 0 -> System.out.println("Keluar dari program.");
                default -> System.out.println("Pilihan tidak valid.");
            }

        } while (pilihan != 0);
    }

    private static void tampilMenu() {
        System.out.println("\n========== MENU TESTING UTILS ==========");
        System.out.println("1. Test Generate Random Kode");
        System.out.println("2. Test Luhn Checksum Generate");
        System.out.println("3. Test Luhn Is Valid");
        System.out.println("4. Test Check Valid Date");
        System.out.println("5. Test Get Voucher");
        System.out.println("0. Keluar");
        System.out.println("========================================");
    }

    // TEST GenerateRandomKode
    private static void testGenerateRandomKode() {
        try {
            int length = bacaInt("Masukkan panjang kode: ");

            String hasil = randomKode.generate(length);

            System.out.println("\nHasil Generate Random Kode");
            System.out.println("Kode   : " + hasil);
            System.out.println("Panjang: " + hasil.length());

        } catch (Exception e) {
            System.out.println("Gagal generate kode: " + e.getMessage());
        }
    }

    // TEST LuhnAlgorithm - chekSumGenerate
    private static void testLuhnGenerate() {
        try {
            String text = bacaText("Masukkan text: ");

            char hasil = luhn.chekSumGenerate(text);

            System.out.println("\nHasil Checksum Generate");
            System.out.println("Text         : " + text);
            System.out.println("Check Digit  : " + hasil);
            System.out.println("Kode Lengkap : " + text + hasil);

        } catch (Exception e) {
            System.out.println("Gagal generate checksum: " + e.getMessage());
        }
    }

    // TEST LuhnAlgorithm - isValid
    private static void testLuhnValid() {
        try {
            String kode = bacaText("Masukkan kode: ");

            boolean hasil = luhn.isValid(kode);

            System.out.println("\nHasil Validasi Luhn");
            System.out.println("Kode  : " + kode);
            System.out.println("Valid : " + hasil);

        } catch (Exception e) {
            System.out.println("Gagal validasi kode: " + e.getMessage());
        }
    }

    // TEST voucher - checkIsValidDate
    private static void testCheckValidDate() {
        try {
            LocalDate timeNow = bacaTanggal("Tanggal sekarang (yyyy-mm-dd): ");
            LocalDate birthday = bacaTanggal("Tanggal ulang tahun (yyyy-mm-dd): ");

            boolean hasil = voucher.checkIsValidDate(timeNow, birthday);

            System.out.println("\nHasil Validasi Tanggal");
            System.out.println("Tanggal Sekarang : " + timeNow);
            System.out.println("Tanggal Ulang Tahun : " + birthday);
            System.out.println("Valid : " + hasil);

        } catch (Exception e) {
            System.out.println("Gagal validasi tanggal: " + e.getMessage());
        }
    }

    // TEST voucher - getVoucer
    private static void testGetVoucher() {
        try {
            LocalDate timeNow = bacaTanggal("Tanggal sekarang (yyyy-mm-dd): ");
            LocalDate birthday = bacaTanggal("Tanggal ulang tahun (yyyy-mm-dd): ");

            String hasil = voucher.getVoucer(timeNow, birthday);

            System.out.println("\nHasil Get Voucher");
            System.out.println("Voucher : " + hasil);

        } catch (InputMismatchException e) {
            System.out.println("Gagal mendapatkan voucher: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // INPUT TEXT
    private static String bacaText(String label) {
        System.out.print(label);
        return in.nextLine().trim();
    }

    // INPUT INTEGER
    private static int bacaInt(String label) {
        try {
            return Integer.parseInt(bacaText(label));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // INPUT TANGGAL
    private static LocalDate bacaTanggal(String label) {
        System.out.print(label);
        return LocalDate.parse(in.nextLine().trim());
    }
}
