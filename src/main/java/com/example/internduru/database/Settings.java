package com.example.internduru.database;

import com.example.internduru.features.DatabaseController;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    private VBox mainLayout;

    public Settings(VBox mainLayout) {
        this.mainLayout = mainLayout;
        products();
    }

    private void products() {
        VBox fileLayout = new VBox();
        fileLayout.setPadding(new Insets(30));

        Path resourcesDirectory = Paths.get("resources");
        String dbPath = resourcesDirectory + "\\db\\Settings.db";
        DatabaseConnector.setUrl(dbPath); //Dosyaya tekrar tekrar bağlanılması mantıklı değil. Zaten DB değişmiyor, bir kez bağlanılıp data çekilse yeterli

        VBox layoutTable = new VBox(10);
        layoutTable.setPadding(new Insets(0, 0, 25, 0));

        String sqlQuery = """
            SELECT p.PLUNo, p.Name as pName, p.Price, d.Name as dName, p.ProductCategory, p.Barcode, p.IsWeighable
            FROM PRODUCT p
            INNER JOIN DEPARTMENT d on d.DepartmentNo = p.DepartmentIndex
        """;

        ObservableList<Map<String, Object>> data = DatabaseHandler.executeCustomQuery(sqlQuery);

        Label tableNameLabel = new Label("Ürünler");
        tableNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        TableView<Map<String, Object>> table = new TableView<>();

        if (!data.isEmpty()) {
            int rowCount = data.size();
            table.setPrefHeight(((rowCount + 2) * 25));

            HashMap<String, String> columnMappings = new HashMap<>();
            columnMappings.put("PLUNo", "PluNo");
            columnMappings.put("pName", "Ürün Adı");
            columnMappings.put("Price", "Fiyat");
            columnMappings.put("dName", "Departman");
            columnMappings.put("ProductCategory", "Kategori");
            columnMappings.put("Barcode", "Barkod");
            columnMappings.put("IsWeighable", "Tartılabilir");

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
