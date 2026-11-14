/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

/**
 *
 * @author vallen
 */
public class SupplierInterface{
    public int id; 
    public String nama;
    public String alamat;
    public String nomorHp;
    public int dihapus;

    public SupplierInterface(String nama, String alamat, String nomorHp) {
        this.nama = nama;
        this.alamat = alamat;
        this.nomorHp = nomorHp;
    }

    public SupplierInterface(int id, String nama, String alamat, String nomorHp, int dihapus) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.nomorHp = nomorHp;
        this.dihapus = dihapus;
    }
}
