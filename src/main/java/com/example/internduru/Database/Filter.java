package com.example.internduru.Database;

import com.example.internduru.Features.DatabaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Filter {

    private final VBox fileLayout;
    private final boolean showSales, showEdits, showCancel;

    private final ArrayList<String> departmentNames = new ArrayList<>();
    private final Map<Integer, String> productNamesMap = new HashMap<>();

    public Filter(VBox fileLayout, String currentFilterType, String input, CheckBox showSales, CheckBox showEdits, CheckBox showCancel,
                  Label salesInfo, Label editInfo, Label cancelInfo) {
        this.showSales = showSales.isSelected();
        this.showEdits = showEdits.isSelected();
        this.showCancel = showCancel.isSelected();
        this.fileLayout = fileLayout;
        chooseFilteringType(input, currentFilterType, salesInfo, editInfo, cancelInfo);
    }

    private void chooseFilteringType(String input, String currentFilterType, Label salesInfo, Label editInfo, Label cancelInfo) {
        getProductsAndDepartments();

        String sqlWhere = "";
        if (!input.isEmpty()) {
            switch (currentFilterType) {
                case ("NAME") -> sqlWhere = "WHERE s.name = '" + input + "'";
                case ("VAT") -> sqlWhere = "WHERE s.vatRate = '" + input + "'";
                case ("PLU") -> sqlWhere = "WHERE s.name = '" + productNamesMap.get(Integer.parseInt(input)) + "'";
            }
        }
        VBox layoutTable = new VBox(10);
        layoutTable.setPadding(new Insets(0, 0, 25, 0));
        filter(layoutTable, salesInfo, editInfo, cancelInfo, sqlWhere);
    }


    private void filter(VBox layoutTable, Label salesInfo, Label editInfo, Label cancelInfo, String sqlWhere) {

        Path resourcesDirectory = Paths.get("resources");
        String dbPath = resourcesDirectory + "\\db\\SFA_DB";
        DatabaseConnector.setUrl(dbPath);

        String sqlQuery = "SELECT " +
                "CASE WHEN d.date IS NOT NULL AND d.date != '' \n" +
                "THEN strftime('%d-%m-%Y', substr(d.date, 1, 4) || '-' || substr(d.date, 5, 2) || '-' || substr(d.date, 7, 2)) || ' ' || d.documentTime \n" +
                "ELSE '' \n" +
                "END AS dateTime, \n " +
                "s.name, s.price, s.quantity " +
                "FROM SaleDetails s " +
                "INNER JOIN Documents d ON d.id = s.documentId " +
                sqlWhere;

        ObservableList<Map<String, Object>> data = DatabaseHandler.executeCustomQuery(sqlQuery);
        TableView<Map<String, Object>> table = new TableView<>();

        if (!data.isEmpty()) {
            int rowCount = data.size();
            table.setPrefHeight(((rowCount + 2) * 25));

            HashMap<String, String> columnMappings = new HashMap<>();
            columnMappings.put("date", "Tarih");
            columnMappings.put("name", "İsim");
            columnMappings.put("price", "Fiyat");
            columnMappings.put("quantity", "Miktar");

            DatabaseController.printTable(table, columnMappings);

            TableColumn<Map<String, Object>, String> operationColumn = new TableColumn<>("İşlem");
            operationColumn.setCellValueFactory(cellData -> {
                String name = (String) cellData.getValue().get("name");
                String operation = "";
                if (productNamesMap.containsValue(name)) {
                    operation = "PLU SATIŞI";
                } else if (departmentNames.contains(name)) {
                    operation = "DEPARTMAN SATIŞI";
                }
                return new SimpleStringProperty(operation);
            });
            table.getColumns().add(operationColumn);

            int totalQuantity = 0;
            double totalPrice = 0;

            for (Map<String, Object> row : data) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");

                String strPrice = row.get("price").toString();
                double price = Integer.parseInt(strPrice) / 1000.00;

                row.put("price", decimalFormat.format(price));

                String strQuantity = row.get("quantity").toString();
                int quantity = Integer.parseInt(strQuantity) / 1000;
                row.put("quantity", quantity);

                totalQuantity += quantity;
                totalPrice += price;
            }


            //Eğer miktar ve toplamı kendi başlıklarının altında aralarında space olacak şekilde basılsın isteniyorsa yorumları kaldırırımy
            salesInfo.setText(showSales ? String.format("%d " +
                    /* "%s " + */
                    "- %.2f", totalQuantity, /* returnSpace(totalQuantity),*/ totalPrice) : "");

            editInfo.setText(showEdits ? "0 - 0,00" : ""); // Bu değerleri nerden alacağımı bilmiyorum
            cancelInfo.setText(showCancel ? "0 - 0,00" : ""); // Bu değerleri nerden alacağımı bilmiyorum

        } else {
            table.setPrefHeight(50);
        }

        table.setItems(data);
        layoutTable.getChildren().addAll(table);
        fileLayout.getChildren().addAll(layoutTable);
    }

    private void getProductsAndDepartments() {
        Path resourcesDirectory = Paths.get("resources");
        String dbPath = resourcesDirectory + "\\db\\Settings.db";
        DatabaseConnector.setUrl(dbPath);

        departmentNames.clear();
        productNamesMap.clear();

        String departmentQuery = "SELECT d.Name FROM DEPARTMENT d";
        ObservableList<Map<String, Object>> departmentData = DatabaseHandler.executeCustomQuery(departmentQuery);
        for (Map<String, Object> row : departmentData) {
            departmentNames.add((String) row.get("Name"));
        }

        String productQuery = "SELECT p.PLUNo, p.Name FROM PRODUCT p";
        ObservableList<Map<String, Object>> productData = DatabaseHandler.executeCustomQuery(productQuery);
        for (Map<String, Object> row : productData) {
            productNamesMap.put((Integer) row.get("PLUNo"), (String) row.get("Name"));
        }
    }

    private String returnSpace (int quantity) { //8 karakter
        String strQuantity = quantity + "";
        String space = "";
        for (int i = strQuantity.length(); i <= 8; i++) {
            space += " ";
        }
        return space;
    }
}
