package com.example.internduru.database;

import com.example.internduru.features.StageHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {

    private DatabaseHandler() { }


    public static ObservableList<Map<String, Object>> executeCustomQuery(String sqlQuery) {
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                } data.add(row);
            }
        } catch (SQLException e) {
            StageHandler.setWarning("SQL sorgusu yürütülemedi", e.getMessage());
        }
        return data;
    }
}
