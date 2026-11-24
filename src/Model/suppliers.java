/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.SupplierInterface;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;

/**
 *
 * @author vallen
 */
public class suppliers extends model {

    public int add(SupplierInterface supplier) throws SQLException {
        this.validator.chekNomorHp(supplier.nomorHp, 12);
        this.validator.chekPanjangNama(supplier.nama, 150);
        this.validator.chekPanjangAlamat(supplier.alamat, 150);

        return this.insert(
                new String[]{"nama", "alamat", "nomor_hp"},
                new String[]{supplier.nama, supplier.alamat, supplier.nomorHp})
                .execute();

    }

    public List<SupplierInterface> get() throws SQLException {
        List<SupplierInterface> supplierList = new ArrayList<>();
        ResultSet res;

        res = this.select().where("dihapus = 0").run();
        while (res.next()) {
            supplierList.add(new SupplierInterface(
                    res.getInt("id"),
                    res.getString("nama"),
                    res.getString("alamat"),
                    res.getString("nomor_hp"),
                    res.getInt("dihapus")
            ));
        }

        return supplierList;
    }

    public int removeSupplier(Integer idSupplier) {
        try {
            return this.updateOnce("dihapus", "1").where("id = %d".formatted(idSupplier.toString())).execute();
        } catch (SQLException ex) {
            System.getLogger(suppliers.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return 0;
    }
}
