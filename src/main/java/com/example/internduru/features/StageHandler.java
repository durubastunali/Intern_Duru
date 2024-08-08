package com.example.internduru.features;

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
    private static Button buttonFileParser = new Button("R-Z Dosyası");
    private static Button buttonSlip = new Button("Slip");
    private static Button buttonDatabase = new Button("Database");
    private static Button buttonSocket = new Button("Server");

    private enum ScreenType {
        MAIN, PARSER, SLIP, DATABASE, CLIENT
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox mainLayout = new VBox();

        setMenuLayout(mainLayout);
        showMainMenu(mainLayout);

        Screen screen = Screen.getPrimary();
        double width = screen.getBounds().getWidth();
        double height = screen.getBounds().getHeight() - 70;
        Scene mainMenuScene = new Scene(mainLayout, width, height);

        primaryStage.setTitle("Hugin");
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    public static void showMainMenu(VBox mainLayout) {
        buttonFileParser.setOnAction(event -> handleButtonAction(ScreenType.PARSER, mainLayout, RZ::new));
        buttonSlip.setOnAction(event -> handleButtonAction(ScreenType.SLIP, mainLayout, Slip::new));
        buttonDatabase.setOnAction(event -> handleButtonAction(ScreenType.DATABASE, mainLayout, DatabaseController::new));
        buttonSocket.setOnAction(event -> handleButtonAction(ScreenType.CLIENT, mainLayout, Client::new));
    }

    private static void handleButtonAction(ScreenType screenType, VBox mainLayout, Consumer<VBox> screenConstructor) {
        if (currentScreen != screenType) {
            if (currentScreen != ScreenType.MAIN) {
                mainLayout.getChildren().clear();
                setMenuLayout(mainLayout);
            }
            screenConstructor.accept(mainLayout);
            currentScreen = screenType;
        }
        updateButtonStyles(screenType);
    }

    private static void updateButtonStyles(ScreenType screenType) {
        String style = "-fx-background-color: #0073e6; -fx-text-fill: white;";
        buttonFileParser.setStyle(screenType == ScreenType.PARSER ? style : "");
        buttonSlip.setStyle(screenType == ScreenType.SLIP ? style : "");
        buttonDatabase.setStyle(screenType == ScreenType.DATABASE ? style : "");
        buttonSocket.setStyle(screenType == ScreenType.CLIENT ? style : "");

    }

    public static void setMenuLayout(VBox mainLayout) {
        double width1 = new Text(buttonFileParser.getText()).getLayoutBounds().getWidth();
        double width2 = new Text(buttonSlip.getText()).getLayoutBounds().getWidth();
        double width3 = new Text(buttonDatabase.getText()).getLayoutBounds().getWidth();
        double width4 = new Text(buttonSocket.getText()).getLayoutBounds().getWidth();


        maxWidth = (int) Math.max(Math.max(width1, Math.max(width2, width3)), width4) + 20;
        buttonFileParser.setPrefWidth(maxWidth);
        buttonSlip.setPrefWidth(maxWidth);
        buttonDatabase.setPrefWidth(maxWidth);
        buttonSocket.setPrefWidth(maxWidth);

        HBox menuBar = new HBox(10);
        menuBar.getChildren().addAll(buttonFileParser, buttonSlip, buttonDatabase, buttonSocket);
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
        fileComboBox.setPrefWidth((double) maxWidth * 3 - labelWidth + 10);

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