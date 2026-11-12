package config;
import java.io.IOException;
import java.util.Scanner;
import java.util.Properties;
import java.io.FileInputStream;
import java.sql.*;

/**
 *
 * @author vallen
 */

class dbConfig{
    String host;
    String port;
    String name;
    String username;
    String password;

    public dbConfig(String host, String port, String name, String username, String password) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.username = username;
        this.password = password;
    }
};

public class Database {
    private String CONFPATH = ".conf";
    private dbConfig dbConf = null;
    private Statement stm;
    private Connection connection;
    
    private void loadConfiguration() throws IOException{
        Properties p = new Properties();
        
        try (FileInputStream in = new FileInputStream(this.CONFPATH)) {
            p.load(in);
        } catch (java.io.FileNotFoundException e) {
            throw new IOException("kehilangan file .conf", e);
        }
        
        this.dbConf = new dbConfig(
            p.getProperty("db.host", "localhost"),
            p.getProperty("db.port", "3306"),
            p.getProperty("db.name"),
            p.getProperty("db.username"),
            p.getProperty("db.password", "")
        );
    }
    
    private void startConnection() throws SQLException{
        String url = "jdbc:mysql://%s:%s/%s"
                .formatted(
                    this.dbConf.host, 
                    this.dbConf.port,
                    this.dbConf.name
                );
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        this.connection = DriverManager.getConnection(
                url, 
                this.dbConf.username,
                this.dbConf.password
        );
    }
    
    private void startStatement() throws SQLException{
        this.stm = this.connection.createStatement();
    }
    
    public void close() throws SQLException{
        this.stm.close();
        this.connection.close();
    }
    
    public Statement start() throws IOException{
        this.loadConfiguration();
        try {
            
            this.startConnection();
            this.startStatement();
        } catch (SQLException ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return this.stm;
    }
}
