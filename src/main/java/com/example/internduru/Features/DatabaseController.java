package com.example.hugin_project.Features;

import com.example.hugin_project.Database.FilterController;
import com.example.hugin_project.Database.SFA;
import com.example.hugin_project.Database.Settings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.Map;
import java.util.function.Consumer;

public class DatabaseController {
    private DatabaseScreenType currentScreen = DatabaseScreenType.DATABASE_MAIN;
    private final VBox mainLayout;
    private final VBox fileLayout = new VBox(); //bu burda olmamalı ama şimdilik bilemedim

    private enum DatabaseScreenType {
        DATABASE_MAIN, SFA, SETTINGS, FILTER
    }

    public DatabaseController(VBox mainLayout) {
        this.mainLayout = mainLayout;
        setLayout();
    }

    private void setLayout() {
        Button buttonSFA = new Button("Satışlar");
        Button buttonSettings = new Button("Ayarlar");
        Button buttonFilter = new Button("Sorgu");

        setDatabaseMenuLayout(buttonSFA, buttonSettings, buttonFilter);
        showDatabaseMenu(buttonSFA, buttonSettings, buttonFilter);
    }

    private void setDatabaseMenuLayout(Button buttonSFA, Button buttonSettings, Button buttonFilter) {
        HBox databaseOptions = new HBox();
        double width1 = new Text(buttonSFA.getText()).getLayoutBounds().getWidth();
        double width2 = new Text(buttonSettings.getText()).getLayoutBounds().getWidth();
        double width3 = new Text(buttonFilter.getText()).getLayoutBounds().getWidth();
        int maxWidth = (int) Math.max(width1, Math.max(width2, width3)) + 20;
        buttonSFA.setPrefWidth(maxWidth);
        buttonSettings.setPrefWidth(maxWidth);
        buttonFilter.setPrefWidth(maxWidth);
        databaseOptions.getChildren().addAll(buttonSFA, buttonSettings, buttonFilter);
        databaseOptions.setPadding(new Insets(0, 30, 0, 30));
        databaseOptions.setStyle("-fx-background-color: #151515;");
        mainLayout.getChildren().add(databaseOptions);
    }

    private void showDatabaseMenu(Button buttonSFA, Button buttonSettings, Button buttonFilter) {
        buttonSFA.setOnAction(event -> handleButtonAction(DatabaseScreenType.SFA, buttonSFA, buttonSettings, buttonFilter, SFA::new));
        buttonSettings.setOnAction(event -> handleButtonAction(DatabaseScreenType.SETTINGS, buttonSFA, buttonSettings, buttonFilter, Settings::new));
        buttonFilter.setOnAction(event -> handleButtonAction(DatabaseScreenType.FILTER, buttonSFA, buttonSettings, buttonFilter, FilterController::new));
    }

    private void handleButtonAction(DatabaseScreenType screenType, Button buttonSFA, Button buttonSettings, Button buttonFilter, Consumer<VBox> screenConstructor) {
        if (currentScreen != screenType) {
            if (currentScreen != DatabaseScreenType.DATABASE_MAIN) {
                mainLayout.getChildren().clear();
                StageHandler.setMenuLayout(mainLayout);
                setDatabaseMenuLayout(buttonSFA, buttonSettings, buttonFilter);
            }
            screenConstructor.accept(mainLayout);
            currentScreen = screenType;
        }
        updateButtonStyles(buttonSFA, buttonSettings, buttonFilter, screenType);
    }

    private void updateButtonStyles(Button buttonSFA, Button buttonSettings, Button buttonFilter, DatabaseScreenType screenType) {
        buttonSFA.setStyle(screenType == DatabaseScreenType.SFA ? "-fx-background-color: #0073e6; -fx-text-fill: white;" : "");
        buttonSettings.setStyle(screenType == DatabaseScreenType.SETTINGS ? "-fx-background-color: #0073e6; -fx-text-fill: white;" : "");
        buttonFilter.setStyle(screenType == DatabaseScreenType.FILTER ? "-fx-background-color: #0073e6; -fx-text-fill: white;" : "");
    }

    public static void printTable (TableView<Map<String, Object>> table, Map<String, String> columnMappings){
        for (Map.Entry<String, String> entry : columnMappings.entrySet()) {
            String columnName = entry.getKey();
            String customName = entry.getValue();
            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(customName);
            column.setCellValueFactory(param -> {
                Object value = param.getValue().get(columnName);
                return new SimpleObjectProperty<>(value != null ? value.toString() : "");
            });
            table.getColumns().add(column);
        }
    }
}
