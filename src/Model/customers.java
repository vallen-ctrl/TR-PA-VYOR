/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import Model.interfaces.customerInterface;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import utils.Validation;
/**
 *
 * @author vallen
 */

public class customers extends model{
    private int panjangKode = 10;
    private int panjangnama = 100;
    private int panjangNomorHp = 12;
  
    public void add (customerInterface cust)
    {
        this.validator.chekPanjangKode(cust.kode, this.panjangKode);
        this.validator.chekPanjangNama(cust.nama, this.panjangnama);
        this.validator.chekNomorHp(cust.nomorHp, this.panjangNomorHp);
        try {
            this.insert(
                    new String[]{"kode", "nama", "tanggal_lahir", "nomor_hp"},
                    new String[]{cust.kode, cust.nama, cust.tanggal_lahir.toString(), cust.nomorHp}).execute();
        } catch (SQLException ex) {
            System.getLogger(customers.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public int removeCustomer(String kode){
        try {
            return this.updateOnce("dihapus", "1").where("kode = %s".formatted(kode)).execute();
        } catch (SQLException ex) {
            System.getLogger(customers.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return 0;
    }
    
    public List<customerInterface> getCustomer(int limit, int Offset){
        List<customerInterface> customer = new ArrayList<>();
        try {
            ResultSet res = this.select().where("dihapus = 0").setLimit(limit).setOffset(Offset).run();
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
            System.getLogger(customers.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return customer;
    }
}
