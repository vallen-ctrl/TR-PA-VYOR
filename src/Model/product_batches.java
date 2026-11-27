/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.product_batchInterface;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author vallen
 */
public class product_batches extends model {

    public void addbatch(product_batchInterface batch) throws SQLException {
        String[] Column = {
            "product_id", "supplier_id",
            "harga_beli", "qty", "qty_sisa",
            "tanggal_pemesanan ", "tanggal_masuk",
            "tanggal_kadaluarsa"
        };

        String[] Values = {
            batch.product_id.toString(), batch.supplier_id.toString(),
            batch.harga_beli.toString(), batch.qty.toString(), batch.qty_sisa.toString(),
            batch.tanggal_pemesanan.toString(), batch.tanggal_masuk.toString(),
            batch.tanggal_kadaluarsa.toString()
        };

        this.insert(Column, Values).execute();
    }
    
    public int deletebatch(String barchId) throws SQLException{
       return this.delete().where("id = "+barchId).execute();
    }

    public int setDitangani(int batchID, boolean ditangani) throws SQLException {
        return this.updateOnce("ditangani", "" + ditangani).where("id = " + batchID).execute();
    }

    public List<product_batchInterface> get(Integer ProductId) throws SQLException {
        List<product_batchInterface> batch = new ArrayList<>();

        ResultSet res = this.select(this.thisTableName() + ".*", "suppliers.nama AS nama_supplier, suppliers.alamat as alamat_supplier")
            .leftJoin("suppliers", "suppliers.id = " + this.thisTableName() + ".supplier_id")
            .where(this.thisTableName()+".product_id = "+ProductId.toString())
            .run();
        while (res.next()) {
            batch.add(this.mapping(res));
        }     
        return batch;
    }
     
    
    public product_batchInterface mapping(ResultSet res) throws SQLException{
        return new product_batchInterface(
                    res.getInt("id"),
                    res.getInt("product_id"),
                    res.getInt("supplier_id"),
                    res.getBigDecimal("harga_beli"),
                    res.getInt("qty"),
                    res.getInt("qty_sisa"),
                    res.getDate("tanggal_pemesanan").toLocalDate(),
                    res.getDate("tanggal_masuk").toLocalDate(),
                    res.getDate("tanggal_kadaluarsa").toLocalDate(),
                    res.getBoolean("ditangani"),
                    res.getString("nama_supplier"),
                    res.getString("alamat_supplier")
            );
    }
}
