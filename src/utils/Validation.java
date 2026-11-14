/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.util.InputMismatchException;

/**
 *
 * @author vallen
 */
public class Validation {

    public void chekPanjangKode(String kode, int maxKode) {
        if (kode.length() > maxKode) {
            throw new InputMismatchException("Hanya menerima kode dengan panjang dibawah 11" + maxKode);
        }
    }

    public void chekPanjangNama(String Nama, int maxNama) {
        if (Nama.length() > maxNama) {
            throw new InputMismatchException("Hanaya menerima nama dengan panjang dibawah 101" + maxNama);
        }
    }

    public void chekNomorHp(String nomorHp, int maxNoHp) {
        if (nomorHp == null || nomorHp.isBlank()) {
            throw new InputMismatchException("Nomor HP tidak boleh kosong");
        }
        String bersih = nomorHp.replaceAll("[\\s-]", "");

        if (!bersih.matches("\\d+")) {
            throw new InputMismatchException("Nomor HP hanya boleh berisi angka");
        }
        if (bersih.length() < 10 || bersih.length() > maxNoHp) {
            throw new InputMismatchException(
                    "Jumlah digit nomor HP harus 10-" + maxNoHp);
        }
        if (!bersih.startsWith("08") && !bersih.startsWith("62")) {
            throw new InputMismatchException("Nomor HP harus diawali 08 atau 62");
        }
    }

    public void chekPanjangAlamat(String Alamat, int maxAlamat) {
        if (Alamat.length() > maxAlamat) {
            throw new InputMismatchException("Hanaya menerima nama dengan panjang dibawah " + maxAlamat);
        }
    }
}
