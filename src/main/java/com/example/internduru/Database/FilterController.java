package com.example.hugin_project.Database;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FilterController {

    private final VBox mainLayout;
    private String currentFilterType = "NAME";

    public FilterController(VBox mainLayout) {
        this.mainLayout = mainLayout;
        VBox fileLayout = new VBox(10);
        setFilterLayout(fileLayout);
        fileLayout.setPadding(new Insets(30));
    }

    private void setFilterLayout(VBox fileLayout) {
        HBox filterLayout = new HBox(80);

        Label labelCommand = new Label();
        TextField filterText = new TextField();
        Button listButton = new Button("Listele");

        labelCommand.setStyle("-fx-font-weight: bold");


        CheckBox sales = new CheckBox("Satış");
        CheckBox edit = new CheckBox("Düzeltme");
        CheckBox cancel = new CheckBox("İptal");

        Label salesInfo = new Label();
        Label editInfo = new Label();
        Label cancelInfo = new Label();

        setLeftLayout(filterLayout, labelCommand, filterText, listButton);
        setRightLayout(filterLayout, sales, edit, cancel, salesInfo, editInfo, cancelInfo);

        fileLayout.getChildren().add(filterLayout);
        listButton.setOnAction(event -> {
            fileLayout.getChildren().clear();
            fileLayout.getChildren().add(filterLayout);
            String input = filterText.getText();
            new Filter(fileLayout, currentFilterType, input, sales, edit, cancel, salesInfo, editInfo, cancelInfo);
        });
        mainLayout.getChildren().add(fileLayout);
    }

    private void setLeftLayout(HBox filterLayout, Label labelCommand, TextField filterText, Button listButton) {
        VBox leftLayout = new VBox(10);
        HBox layoutFilterOptions = new HBox(10);
        HBox layoutEnterFilter = new HBox(10);

        Label filter = new Label("Sorgu Çalıştır");
        Button buttonName = new Button("Kalem Adı");
        Button buttonVAT = new Button("KDV");
        Button buttonPLU = new Button("PLU");

        filter.setStyle("-fx-font-weight: bold");


        setButtonActions(buttonName, buttonVAT, buttonPLU, labelCommand);
        buttonName.fire();

        layoutFilterOptions.getChildren().addAll(buttonName, buttonVAT, buttonPLU);
        layoutEnterFilter.getChildren().addAll(filterText, listButton);
        leftLayout.getChildren().addAll(filter, layoutFilterOptions, labelCommand, layoutEnterFilter);
        filterLayout.getChildren().add(leftLayout);
    }

    private void setRightLayout(HBox filterLayout, CheckBox sales, CheckBox edit, CheckBox cancel,
                                Label salesInfo, Label editInfo, Label cancelInfo) {
        HBox rightLayout = new HBox(30);
        VBox layoutCheckBox = new VBox(15);
        VBox layoutAmountTotal = new VBox(15);

        Label labelFilter = new Label("Filtreler");
        Label amountTotal = new Label("Miktar - Toplam");

        labelFilter.setStyle("-fx-font-weight: bold");
        amountTotal.setStyle("-fx-font-weight: bold");



        layoutCheckBox.getChildren().addAll(labelFilter, sales, edit, cancel);
        layoutAmountTotal.getChildren().addAll(amountTotal, salesInfo, editInfo, cancelInfo);
        rightLayout.getChildren().addAll(layoutCheckBox, layoutAmountTotal);
        filterLayout.getChildren().add(rightLayout);
    }

    private void setButtonActions(Button buttonName, Button buttonVAT, Button buttonPLU, Label labelCommand) {
        buttonName.setOnAction(event -> {
            labelCommand.setText("DEPARTMAN ya da PLU Adı");
            currentFilterType = "NAME";
            updateButtonStyles(buttonName, buttonVAT, buttonPLU);
        });
        buttonVAT.setOnAction(event -> {
            labelCommand.setText("KDV değeri");
            currentFilterType = "VAT";
            updateButtonStyles(buttonVAT, buttonName, buttonPLU);
        });
        buttonPLU.setOnAction(event -> {
            labelCommand.setText("PLU numarası");
            currentFilterType = "PLU";
            updateButtonStyles(buttonPLU, buttonName, buttonVAT);
        });
    }

    private void updateButtonStyles(Button selectedButton, Button... otherButtons) {
        selectedButton.setStyle("-fx-background-color: #0073e6; -fx-text-fill: white;");
        for (Button button : otherButtons) {
            button.setStyle("");
        }
    }
}
