
package lk.ijse.metalworkshopmanagementsystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    final String DB_URL = "jdbc:mysql://localhost:3306/metal_workshop_db";
    final String DB_USERNAME = "root";
    final String DB_PASSWORD = "vishva";

    private static DBConnection dbConnection;
    private Connection conn;

    private DBConnection() throws SQLException {

        conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    public static DBConnection getInstance() throws SQLException {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() throws SQLException {
        return conn;
    }

}
