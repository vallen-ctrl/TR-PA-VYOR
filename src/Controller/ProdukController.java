/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import java.util.List;
import Model.products;
import Model.interfaces.productInterface;
import Model.interfaces.categoriesInterface;
import Model.categories;
import java.sql.SQLException;
/**
 *
 * @author vallen
 */
public class ProdukController {
    private products prod = new products();
    private categories kategori = new categories(); 
    public List<productInterface> get() throws SQLException{
        return prod.getAll();
    }
    
    public int AddProduct(productInterface insertProduk) throws SQLException{
        return this.prod.add(insertProduk);
    }
    
    public List<categoriesInterface> getCategories() throws SQLException{
        return this.kategori.get();
    }
}
