package com.example.internduru.Database;

import com.example.internduru.Features.StageHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static String url;

    public static void setUrl(String dbUrl) {
        url = "jdbc:sqlite:" + dbUrl;
    }

    public static Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            StageHandler.setWarning("Database ile bağlantı kurulamadı", e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}
