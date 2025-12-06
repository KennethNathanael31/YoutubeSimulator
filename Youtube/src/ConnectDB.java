import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
public class ConnectDB {
    public static ConnectDB instance = null;
    private Statement stat;
    private Connection connection;

    public static ConnectDB getInstance() {
        if (instance == null) {
            try {
                instance = new ConnectDB();
            } catch (SQLException e) {
                System.out.println("ERROR");
            }
        }
        return instance;
    }
    
    private ConnectDB() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=Youtube;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
        connection = DriverManager.getConnection(connectionUrl);        
        stat = connection.createStatement();
    }

    public Connection getConnection() {
        return connection;
    }
    
    public ResultSet getTable(PreparedStatement pStat) throws SQLException {
        return pStat.executeQuery();
    }

    public void updateTable(PreparedStatement pStat) throws SQLException {
        pStat.executeUpdate();
    }

    public ResultSet getTable(String query) throws SQLException {
        return stat.executeQuery(query);
    }
}
