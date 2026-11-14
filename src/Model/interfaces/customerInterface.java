/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.time.LocalDate;

/**
 *
 * @author vallen
 */
public class customerInterface{
    public int id;
    public String kode;
    public String nama;
    public LocalDate tanggal_lahir;
    public String nomorHp;
    public LocalDate tanggal_daftar;
    public int dihapus;

    public customerInterface(String kode, String nama, LocalDate tanggal_lahir, String nomorHp) {
        this.kode = kode;
        this.nama = nama;
        this.tanggal_lahir = tanggal_lahir;
        this.nomorHp = nomorHp;
    }

    public customerInterface(int id, String kode, String nama, LocalDate tanggal_lahir, String nomorHp, LocalDate tanggal_daftar, int dihapus) {
        this.id = id;
        this.kode = kode;
        this.nama = nama;
        this.tanggal_lahir = tanggal_lahir;
        this.nomorHp = nomorHp;
        this.tanggal_daftar = tanggal_daftar;
        this.dihapus = dihapus;
    }
}
