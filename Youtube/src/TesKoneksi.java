
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author vandy
 */
public class TesKoneksi {
    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TesKoneksi.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=Youtube;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
        Connection connection = DriverManager.getConnection(connectionUrl);
        
        Statement stat = connection.createStatement();
        String query = "SELECT * FROM Pengguna";
        ResultSet rs = stat.executeQuery(query);
        while (rs.next()) {
            System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5));
        }
    }
}

