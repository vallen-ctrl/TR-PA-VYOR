/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import java.util.List;
import java.util.ArrayList;
import Model.products;
import Model.interfaces.productInterface;
import java.sql.SQLException;
/**
 *
 * @author vallen
 */
public class ProdukController {
    private products prod = new products();
    public List<productInterface> get() throws SQLException{
        return prod.getAll();
    }
    
    public int AddProduct(productInterface insertProduk) throws SQLException{
        return this.prod.add(insertProduk);
    }
}
