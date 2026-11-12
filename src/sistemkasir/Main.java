/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistemkasir;

import java.sql.*;
import Model.Computer;

/**
 *
 * @author vallen
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        ResultSet res = new Computer().setQuery("SELECT * from computer").run();
        while (res.next()) {
            String nama = res.getString("name");
            System.out.println(nama);
        }
    }

}
