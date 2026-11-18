/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author ricor
 */

/**
 * Data satu baris tabel `returns` (header retur).
 * Satu konstruktor tanpa id untuk insert, satu dengan id untuk data hasil baca.
 */

public class returnsInterface {
    public Long id;
    public Long transaction_id;
    public LocalDateTime waktu;
    public String alasan;
    public BigDecimal total_refund;

    // Untuk insert (id dan waktu diisi database)
    public returnsInterface(Long transaction_id, String alasan, BigDecimal total_refund) {
        if (alasan == null || alasan.isBlank()) {
            throw new IllegalArgumentException("alasan retur wajib diisi");
        }
        if (total_refund == null || total_refund.signum() < 0) {
            throw new IllegalArgumentException("total refund tidak boleh negatif");
        }
        this.transaction_id = transaction_id;
        this.alasan = alasan;
        this.total_refund = total_refund;
    }

    // Untuk data hasil baca dari database
    public returnsInterface(Long id, Long transaction_id, LocalDateTime waktu,
            String alasan, BigDecimal total_refund) {
        this(transaction_id, alasan, total_refund);
        this.id = id;
        this.waktu = waktu;
    }
}
