package com.campus.lostfound.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/campus_lostfound?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "Anushkag31.!"; // common default, can override with env variable

    static {
        try {
            // Load the MySQL JDBC driver class explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        if (url == null || url.trim().isEmpty()) {
            url = DEFAULT_URL;
        }

        String user = System.getenv("DB_USER");
        if (user == null || user.trim().isEmpty()) {
            user = DEFAULT_USER;
        }

        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            password = DEFAULT_PASSWORD;
        }

        return DriverManager.getConnection(url, user, password);
    }
}
