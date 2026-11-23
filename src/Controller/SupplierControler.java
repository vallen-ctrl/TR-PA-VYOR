/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import Model.suppliers;
import java.util.List;
import java.util.ArrayList;
import Model.interfaces.SupplierInterface;
import java.sql.SQLException;
/**
 *
 * @author vallen
 */
public class SupplierControler {
    public suppliers suply = new suppliers();
    
    public List<SupplierInterface> getAll(){
        return suply.get();
    }
    
    public int updateSupplier(SupplierInterface supplierToUpdate) throws SQLException{
        return suply.update(
                new String[]{"nama", "alamat", "nomor_hp"},
                new String[]{supplierToUpdate.nama, supplierToUpdate.alamat, supplierToUpdate.nomorHp}
        ).where("id = "+supplierToUpdate.id).execute();
    }
    
    public int addSupplier(SupplierInterface supplierToAdd) throws SQLException{
        return suply.add(supplierToAdd);
    }
}
