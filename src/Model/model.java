/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import config.Database;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author vallen
 */
public abstract class model {
    private String tabel = getClass().getSimpleName();
    private Statement stm = new Database().start(); 
    private String colum = "*";
    private List<String> conditions = new ArrayList<>(); 
    
    public model select(String... Column){
        if(Column.length > 1) this.colum = String.join(",", Column);
        return this;
    }
    
    public model where(String... Conditions){
        for(String Condition : Conditions){
            this.conditions.add(Condition);
        }
        return this;
    }
}
