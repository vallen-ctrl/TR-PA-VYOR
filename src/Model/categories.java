/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.interfaces.categoriesInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author vallen
 */
public class categories extends model {
    public int add(categoriesInterface categories) throws SQLException{
        String[] Column = {"nama", "dihapus"};
        String[] Values = {categories.name, categories.dihapus ? "1":"0"};
        
        return this.insert(Column, Values).execute();
    }
    
    public int delete(int categoriesid) throws SQLException{
        return this.updateOnce("dihapus", "1").where("id = "+categoriesid).execute();
    }
    
    public List<categoriesInterface> get() throws SQLException{
        List<categoriesInterface> categoris = new ArrayList<>();
        ResultSet res = this.select().run();
        
        while(res.next()){
            categoris.add(new categoriesInterface(
                    res.getString("nama"),
                    res.getBoolean("dihapus")
            ));
        }
        
        return categoris;
    }
}
