/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.time.LocalDate;
import java.util.InputMismatchException;

/**
 *
 * @author vallen
 */
public class vouchersInterface {
    public Integer id;
    public Integer customer_id;
    public String kode;
    public String jenis;
    public Float persen_diskon;
    public LocalDate berlaku_dari;
    public LocalDate berlaku_sampai;
    public String status;
    
    private void statusChek(String status){
        String[] statusEnum = {"aktif", "dipakai", "kadaluarsa"};
        for(String stat: statusEnum){
            if(stat.equals(status)) return;
        }
        throw new InputMismatchException("tidak menerima status [diterima: aktif, dipakai, kadaluarsa] -> W"+status);
    }

    public vouchersInterface(Integer customer_id, String kode, String jenis, Float persen_diskon, LocalDate berlaku_dari, LocalDate berlaku_sampai, String status) {
        this.customer_id = customer_id;
        this.kode = kode;
        this.jenis = jenis;
        this.persen_diskon = persen_diskon;
        this.berlaku_dari = berlaku_dari;
        this.berlaku_sampai = berlaku_sampai;
        this.statusChek(status);
        this.status = status;
    }
    
    public vouchersInterface(Integer id, Integer customer_id, String kode, String jenis, Float persen_diskon, LocalDate berlaku_dari, LocalDate berlaku_sampai, String status) {
        this.id = id;
        this.customer_id = customer_id;
        this.kode = kode;
        this.jenis = jenis;
        this.persen_diskon = persen_diskon;
        this.berlaku_dari = berlaku_dari;
        this.berlaku_sampai = berlaku_sampai;
        this.statusChek(status);
        this.status = status;
    }
}
