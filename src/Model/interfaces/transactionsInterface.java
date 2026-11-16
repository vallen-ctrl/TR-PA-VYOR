/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
/**
 *
 * @author ricor
 */
public class transactionsInterface {
    public Long id;
    public String kode;
    public LocalDateTime waktu;
    public Integer customer_id;   // boleh null (pembeli umum)
    public Integer voucher_id;    // boleh null (tanpa voucher)
    public BigDecimal sub_total;
    public BigDecimal diskon_voucher;
    public BigDecimal pajak_persen;
    public BigDecimal pajak;
    public BigDecimal grand_total;
    public BigDecimal bayar;
    public BigDecimal kembalian;
    public String status;

    private void statusChek(String status) {
        String[] statusEnum = {"selesai", "dibatalkan", "refund"};
        for (String stat : statusEnum) {
            if (stat.equals(status)) return;
        }
        throw new InputMismatchException("tidak menerima status [diterima: selesai, dibatalkan, refund] -> " + status);
    }

    // Untuk insert (id dan waktu diisi database)
    public transactionsInterface(Integer customer_id, Integer voucher_id,
            BigDecimal sub_total, BigDecimal diskon_voucher, BigDecimal pajak_persen,
            BigDecimal pajak, BigDecimal grand_total, BigDecimal bayar,
            BigDecimal kembalian, String status) {
        this.customer_id = customer_id;
        this.voucher_id = voucher_id;
        this.sub_total = sub_total;
        this.diskon_voucher = diskon_voucher;
        this.pajak_persen = pajak_persen;
        this.pajak = pajak;
        this.grand_total = grand_total;
        this.bayar = bayar;
        this.kembalian = kembalian;
        this.statusChek(status);
        this.status = status;
    }

    // Untuk data hasil baca dari database
    public transactionsInterface(Long id, String kode, LocalDateTime waktu,
            Integer customer_id, Integer voucher_id,
            BigDecimal sub_total, BigDecimal diskon_voucher, BigDecimal pajak_persen,
            BigDecimal pajak, BigDecimal grand_total, BigDecimal bayar,
            BigDecimal kembalian, String status) {
        this(customer_id, voucher_id, sub_total, diskon_voucher, pajak_persen,
                pajak, grand_total, bayar, kembalian, status);
        this.id = id;
        this.kode = kode;
        this.waktu = waktu;
    }
}