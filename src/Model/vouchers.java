/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.vouchersInterface;

/**
 *
 * @author vallen
 */
public class vouchers extends model {

    public void add(vouchersInterface voucher) {
        this.validator.chekPanjangKode(voucher.kode, 5);
        this.insert(
                new String[]{"customer_id", "kode", "jenis", "persen_diskon", "berlaku_dari", "berlaku_sampai", "status"},
                new String[]{voucher.customer_id.toString(), voucher.kode, voucher.jenis, voucher.berlaku_dari.toString(), voucher.berlaku_sampai.toString(), voucher.status}
        );
    }
}
