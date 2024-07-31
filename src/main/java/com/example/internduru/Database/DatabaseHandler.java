package com.example.hugin_project.Database;

import com.example.hugin_project.Features.StageHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {

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
