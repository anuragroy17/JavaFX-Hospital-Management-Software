package com.anuragroy.entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//THIS CLASS IS USED TO RETURN THE DRIVER CONNECTING THE MYSQL DATABASE

public class DBConnection {
    private static final String USERNAME = "dbuser";
    private static final String PASSWORD = "dbpassword";
    private static final String CONN = "jdbc:mysql://localhost/hospital";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONN, USERNAME, PASSWORD);
    }
}
