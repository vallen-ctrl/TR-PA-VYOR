/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import Model.products;
import Model.interfaces.productInterface;
import Model.product_batches;
import Model.interfaces.product_batchInterface;
import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author vallen
 */
public class DetailProdukController {
    private product_batches batch = new product_batches();
    private products prod = new products();
    
    public productInterface getProdukByNumber(String Number) throws SQLException{
        return this.prod.getProductByNumber(Number);
    }
    public List<product_batchInterface> getBatches(int produkId) throws SQLException{
        return this.batch.get(produkId);
    }
}
