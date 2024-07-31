package com.example.hugin_project.Features;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class StageHandler extends Application {

    private static ScreenType currentScreen = ScreenType.MAIN;
    private static int maxWidth;
    private static Button buttonFileParser, buttonSlip, buttonDatabase;

    private enum ScreenType {
        MAIN, PARSER, SLIP, DATABASE
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox mainLayout = new VBox();
        buttonFileParser = new Button("Z - R Okuma");
        buttonSlip = new Button("Slip Bastırma");
        buttonDatabase = new Button("Database Görüntüleme");

        setMenuLayout(mainLayout);
        showMainMenu(mainLayout);

        Screen screen = Screen.getPrimary();
        double width = screen.getBounds().getWidth();
        double height = screen.getBounds().getHeight() - 70;
        Scene mainMenuScene = new Scene(mainLayout, width, height);

        primaryStage.setTitle("App");
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    public static void showMainMenu(VBox mainLayout) {
        buttonFileParser.setOnAction(event -> handleButtonAction(ScreenType.PARSER, buttonFileParser, buttonSlip, buttonDatabase, mainLayout, RZ::new));
        buttonSlip.setOnAction(event -> handleButtonAction(ScreenType.SLIP, buttonFileParser, buttonSlip, buttonDatabase, mainLayout, Slip::new));
        buttonDatabase.setOnAction(event -> handleButtonAction(ScreenType.DATABASE, buttonFileParser, buttonSlip, buttonDatabase, mainLayout, DatabaseController::new));
    }

    private static void handleButtonAction(ScreenType screenType, Button buttonFileParser, Button buttonSlip, Button buttonDatabase, VBox mainLayout, Consumer<VBox> screenConstructor) {
        if (currentScreen != screenType) {
            if (currentScreen != ScreenType.MAIN) {
                mainLayout.getChildren().clear();
                setMenuLayout(mainLayout);
            }
            screenConstructor.accept(mainLayout);
            currentScreen = screenType;
        }
        updateButtonStyles(buttonFileParser, buttonSlip, buttonDatabase, screenType);
    }

    private static void updateButtonStyles(Button buttonFileParser, Button buttonSlip, Button buttonDatabase, ScreenType screenType) {
        buttonFileParser.setStyle(screenType == ScreenType.PARSER ? "-fx-background-color: #0073e6; -fx-text-fill: white;" : "");
        buttonSlip.setStyle(screenType == ScreenType.SLIP ? "-fx-background-color: #0073e6; -fx-text-fill: white;" : "");
        buttonDatabase.setStyle(screenType == ScreenType.DATABASE ? "-fx-background-color: #0073e6; -fx-text-fill: white;" : "");
    }

    public static void setMenuLayout(VBox mainLayout) {
        double width1 = new Text(buttonFileParser.getText()).getLayoutBounds().getWidth();
        double width2 = new Text(buttonSlip.getText()).getLayoutBounds().getWidth();
        double width3 = new Text(buttonDatabase.getText()).getLayoutBounds().getWidth();

        maxWidth = (int) Math.max(width1, Math.max(width2, width3)) + 20;
        buttonFileParser.setPrefWidth(maxWidth);
        buttonSlip.setPrefWidth(maxWidth);
        buttonDatabase.setPrefWidth(maxWidth);

        HBox menuBar = new HBox(10);
        menuBar.getChildren().addAll(buttonFileParser, buttonSlip, buttonDatabase);
        menuBar.setPadding(new Insets(30));
        menuBar.setStyle("-fx-background-color: #151515;");
        mainLayout.getChildren().add(menuBar);
    }

    public static void setFileLayout(VBox mainLayout, VBox fileLayout, ComboBox<String> fileComboBox, String feature) {
        HBox searchLayout = new HBox(10);
        searchLayout.setAlignment(Pos.CENTER_LEFT);
        searchLayout.setPadding(new Insets(0, 30, 30, 30));
        searchLayout.setStyle("-fx-background-color: #151515;");


        Label chooseFileLabel = new Label(feature + " dosyası seçin:");
        chooseFileLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");


        Text text = new Text(chooseFileLabel.getText());
        text.setStyle(chooseFileLabel.getStyle());
        new Scene(new Group(text));
        text.applyCss();

        int labelWidth = (int) text.getLayoutBounds().getWidth();
        fileComboBox.setPrefWidth(maxWidth * 3 - labelWidth + 10);

        searchLayout.getChildren().addAll(chooseFileLabel, fileComboBox);
        fileLayout.setPadding(new Insets(30));
        ScrollPane scrollPane = new ScrollPane(fileLayout);
        scrollPane.setFitToWidth(true);
        mainLayout.getChildren().addAll(searchLayout, scrollPane);
    }

    public static void setWarning(String warning, String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText(warning + ":\n" + errorMessage);
            alert.showAndWait();
        });
    }
}
