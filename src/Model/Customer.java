/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author vallen
 */

class customerInterface{
    int id;
    String kode;
    String nama;
    LocalDate tanggal_lahir;
    String nomorHp;
    LocalDate tanggal_daftar;
    int dihapus;

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

public class Customer extends model{
    private int panjangKode = 10;
    private int panjangama = 100;
    private int panjangNomorHp = 12;
    
    private void chekPanjangKode(String kode){
        if(kode.length() > this.panjangKode) 
            throw new InputMismatchException("Hanya menerima kode dengan panjang dibawah 11");
    }
    private void chekPanjangNama(String Nama){
        if(Nama.length() > this.panjangama)
            throw new InputMismatchException("Hanaya menerima nama dengan panjang dibawah 101");
    }
    private void chekNomorHp(String NomorHp){
        if(NomorHp.length() >= 10 || NomorHp.length() <= this.panjangNomorHp)
            throw new InputMismatchException("Jumlah nomor hp tidak valid, hanya menerima nomor hp dengan jumlah digit 10-12");
    }
    
    public void addCustomer (
            String Kode, 
            String Nama, 
            LocalDate TanggalLahir, 
            String NomorHp)
    {
        this.chekPanjangKode(Kode);
        this.chekPanjangNama(Nama);
        this.chekNomorHp(NomorHp);
        try {
            this.insert(
                    new String[]{"kode", "nama", "tanggal_lahir", "nomor_hp"},
                    new String[]{Kode, Nama, TanggalLahir.toString(), NomorHp}).execute();
        } catch (SQLException ex) {
            System.getLogger(Customer.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public int removeCustomer(String kode){
        try {
            return this.updateOnce("dihapus", "1").execute();
        } catch (SQLException ex) {
            System.getLogger(Customer.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return 0;
    }
    
    public List<customerInterface> getCustomer(int limit, int Offset){
        List<customerInterface> customer = new ArrayList<>();
        try {
            ResultSet res = this.select().setLimit(limit).setOffset(Offset).run();
            while(res.next()){
                customer.add(new customerInterface(
                        res.getInt("id"),
                        res.getString("kode"),
                        res.getString("nama"),
                        res.getDate("tanggal_lahir").toLocalDate(),
                        res.getString("nomor_hp"),
                        res.getDate("tanggal_daftar").toLocalDate(),
                        res.getInt("dihapus")
                ));
            }
        } catch (SQLException ex) {
            System.getLogger(Customer.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return customer;
    }
}
