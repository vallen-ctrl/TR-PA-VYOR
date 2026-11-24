/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.interfaces.productInterface;
import Model.interfaces.transaction_itemInterface;
import Model.interfaces.transactionsInterface;
import Model.products;
import Model.transaction_items;
import Model.transactions;
import Model.vouchers;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author ricor
 */
public class TransaksiController {

    public record ItemKeranjangInput(
            String kodeBarang,
            String namaBarang,
            int qty,
            String satuan,
            BigDecimal hargaSatuan
    ) {}

    public record HasilSimpanTransaksi(
            boolean berhasil,
            String pesan,
            long transactionId
    ) {}

    public productInterface cariProdukByNomor(String nomor) throws SQLException {
        return new products().getProductByNumber(nomor);
    }

    public HasilSimpanTransaksi simpanTransaksi(
            transactionsInterface trx,
            List<ItemKeranjangInput> items
    ) {
        try {
            transactions trxModel = new transactions();
            long trxId = trxModel.add(trx);
            if (trxId <= 0) {
                return new HasilSimpanTransaksi(false, "Gagal menyimpan transaksi ke database!", -1);
            }

            products prodModel = new products();
            transaction_items itemModel = new transaction_items();

            for (ItemKeranjangInput in : items) {
                productInterface prod = prodModel.getProductByNumber(in.kodeBarang());
                int productId = (prod != null && prod.id != null) ? prod.id : 1;

                transaction_itemInterface item = new transaction_itemInterface(
                        trxId,
                        productId,
                        in.kodeBarang(),
                        in.namaBarang(),
                        in.qty(),
                        in.satuan(),
                        in.hargaSatuan()
                );
                itemModel.add(item);
                prodModel.kurangiStok(productId, in.qty());
            }

            if (trx.voucher_id != null) {
                new vouchers().tandaiDipakai(trx.voucher_id);
            }

            return new HasilSimpanTransaksi(true, "Transaksi berhasil disimpan.", trxId);
        } catch (Exception ex) {
            return new HasilSimpanTransaksi(
                    false,
                    "Error menyimpan transaksi: " + ex.getMessage(),
                    -1
            );
        }
    }

    public List<transactionsInterface> daftarTransaksi(
            String status,
            LocalDate dari,
            LocalDate sampai,
            Integer customerId,
            int limit,
            int offset
    ) {
        return new transactions().daftar(status, dari, sampai, customerId, limit, offset);
    }

    public List<transaction_itemInterface> detailItemTransaksi(long transactionId) {
        return new transaction_items().byTransaksi(transactionId);
    }
}
