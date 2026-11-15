/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.categoriesInterface;
import Model.interfaces.productInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

/**
 *
 * @author vallen
 */
public class products extends model{
    public int add(productInterface product) throws SQLException{
        String[] Column = {
            "nomor", "nama", "category_id",
            "supplier_id", "satuan", "harga_beli",
            "harga_jual", "stok", "terakhir_disuplai",
            "dihapus"
        };
        
        String[] Values = {
            product.nomor, product.nama, product.category_id.toString(),
            product.supplier_id.toString(), product.satuan, product.harga_beli.toString(),
            product.harga_jual.toString(), product.stok.toString(), product.terakhir_disuppli.toString(),
            product.dihapus ? "1" : "0"
        };
        return this.insert(Column, Values).execute();
    }
    
    public int remove(int Product) throws SQLException{
        return this.updateOnce("dihapus", "1").where("id = "+Product).execute();
    }
    
    public List<productInterface> getAll(int limit, int offset) throws SQLException{
        List<productInterface> product = new ArrayList<>();
       
        ResultSet res = this.select(
                "products.id", "products.nomor", "products.nama","c.nama as categories","products.category_id", "s.nama as supplier","products.supplier_id", "products.satuan", "products.harga_beli",
                "products.harga_jual", "products.stok", "products.terakhir_disuplai", "products.dihapus"
        ).setLimit(limit).setOffset(offset)
                .leftJoin("categories c","c.id = products.category_id")
                .leftJoin("suppliers s", "s.id = products.supplier_id")
                .where("products.dihapus = 0")
                .run();
        
        while(res.next()){
            product.add(new productInterface(
                    res.getInt("id"),
                    res.getString("nomor"),
                    res.getString("nama"),
                    res.getInt("category_id"),
                    res.getInt("supplier_id"),
                    res.getString("satuan"),
                    res.getDouble("harga_beli"),
                    res.getDouble("harga_jual"),
                    res.getInt("stok"),
                    res.getDate("terakhir_disuplai").toLocalDate(),
                    res.getBoolean("dihapus"),
                    res.getString("categories"),
                    res.getString("supplier")
            ));
        }
        
        return product;
    }
}
