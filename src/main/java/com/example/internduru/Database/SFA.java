package com.example.internduru.Database;

import com.example.internduru.Features.DatabaseController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SFA {

    private final VBox mainLayout;

    public SFA(VBox mainLayout) {
        this.mainLayout = mainLayout;
        documentsAndPayments();
    }

    private void documentsAndPayments() {
        VBox fileLayout = new VBox();
        fileLayout.setPadding(new Insets(30));

        Path resourcesDirectory = Paths.get("resources");
        String dbPath = resourcesDirectory + "\\db\\SFA_DB";
        DatabaseConnector.setUrl(dbPath);

        VBox layoutTable = new VBox(10);
        layoutTable.setPadding(new Insets(0, 0, 25, 0));

        String sqlQuery = "SELECT d.id, d.eDocNumber, \n" +
                "CASE WHEN d.date IS NOT NULL AND d.date != '' \n" +
                "THEN strftime('%d-%m-%Y', substr(d.date, 1, 4) || '-' || substr(d.date, 5, 2) || '-' || substr(d.date, 7, 2)) || ' ' || d.documentTime \n" +
                "ELSE '' \n" +
                "END AS dateTime, \n" +
                "d.totalAmount / 1000.0 AS totalAmount, d.totalVat, \n" +
                "SUM(CASE WHEN p.paymentType = 1 THEN p.amount ELSE 0 END) / 1000.0 AS cashAmount, \n" +
                "SUM(CASE WHEN p.paymentType = 2 THEN p.amount ELSE 0 END) / 1000.0 AS creditAmount, \n" +
                "SUM(CASE WHEN p.paymentType = 11 THEN p.amount ELSE 0 END) / 1000.0 AS chequeAmount \n" +
                "FROM Documents d \n" +
                "INNER JOIN Payments p ON p.documentId = d.id \n" +
                "GROUP BY d.id, d.eDocNumber, d.date, d.totalAmount, d.totalVat\n";

        ObservableList<Map<String, Object>> data = DatabaseHandler.executeCustomQuery(sqlQuery);

        Label tableNameLabel = new Label("Fiş");
        tableNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        TableView<Map<String, Object>> table = new TableView<>();

        if (!data.isEmpty()) {
            int rowCount = data.size();
            table.setPrefHeight(((rowCount + 2) * 25));


            HashMap<String, String> columnMappings = new HashMap<>();
            columnMappings.put("dateTime", "Tarih");
            columnMappings.put("totalAmount", "Toplam");
            columnMappings.put("totalVat", "Toplam KDV");
            columnMappings.put("cashAmount", "Nakit Tutarı");
            columnMappings.put("creditAmount", "Kredi Tutarı");
            columnMappings.put("chequeAmount", "Çek Tutarı");


            String[] eDocParts = {"Ej No", "Z No", "Fiş No"};
            for (int i = 0; i < eDocParts.length; i++) {
                final int index = i;
                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(eDocParts[i]);
                column.setCellValueFactory(param -> {
                    String eDocNumber = (String) param.getValue().get("eDocNumber");
                    if (eDocNumber != null) {
                        String[] splitParts = eDocNumber.split("-");
                        return new SimpleObjectProperty<>(splitParts.length > index ?
                                Integer.parseInt(splitParts[index].replaceAll("[^0-9]", "")) : "");
                    }
                    return new SimpleObjectProperty<>("");
                });
                table.getColumns().add(column);
            }
            DatabaseController.printTable(table, columnMappings);


        } else {
            table.setPrefHeight(50);
        }
        table.setItems(data);
        layoutTable.getChildren().addAll(tableNameLabel, table);
        fileLayout.getChildren().addAll(layoutTable);
        ScrollPane scrollPane = new ScrollPane(fileLayout);
        mainLayout.getChildren().add(scrollPane);
    }
}
