/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.vouchersInterface;
import java.sql.SQLException;

/**
 *
 * @author vallen
 */
public class vouchers extends model {
    private String[] status = {"aktif", "dipakai", "kadaluarsa"};
    
    public void add(vouchersInterface voucher) {
        this.validator.chekPanjangKode(voucher.kode, 5);
        try {
            this.insert(
                    new String[]{"customer_id", "kode", "jenis", "persen_diskon", "berlaku_dari", "berlaku_sampai", "status"},
                    new String[]{voucher.customer_id.toString(), voucher.kode, voucher.jenis, voucher.berlaku_dari.toString(), voucher.berlaku_sampai.toString(), voucher.status}
            ).execute();
        } catch (SQLException ex) {
            System.getLogger(vouchers.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public int remove(String kode){
        try {
            return this.delete().where("kode = "+kode).execute();
        } catch (SQLException ex) {
            System.getLogger(vouchers.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return -1;
    }
    
    /**
     * 
     * @param Status 0 untuk aktif, 1 untuk dipakai, 2 untuk kadaluarsa
     * @return 
     */
    public int updateStatus(int Status){
        try {
            return this.updateOnce("status", this.status[Status]).execute();
        } catch (SQLException ex) {
            System.getLogger(vouchers.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return -1;
    }
}
