/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

/**
 *
 * @author vallen
 */
public class categoriesInterface {
    public Integer id; 
    public String name;
    public Boolean dihapus;

    public categoriesInterface(String name, boolean dihapus) {
        this.name = name;
        this.dihapus = dihapus;
    }

    public categoriesInterface(int id, String name, boolean dihapus) {
        this.id = id;
        this.name = name;
        this.dihapus = dihapus;
    }
    
    
}
