package com.example.internduru.features;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final VBox mainLayout;
    private final Button buttonSend = new Button("Send");

    private Socket socket = null;
    private TextField tfMessageType = new TextField();
    private TextField tfData = new TextField();
    private Label sent = new Label("NONE");
    private Label received = new Label("NONE");


    public Client(VBox mainLayout) {
        this.mainLayout = mainLayout;
        startConnection();
    }

    private void startConnection() {
        setLayout();
        buttonSend.setOnAction(event -> connect());
    }

    private void connect() {
        new Thread(() -> {
            try {
                socket = new Socket("192.168.50.154", 1234);
                PrintWriter inputServer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader outputServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                JSONObject clientMessage = new JSONObject();
                clientMessage.put("messageType", tfMessageType.getText());
                clientMessage.put("value", stringToHex(tfData.getText()));

                inputServer.println(clientMessage);

                String textFromServer = outputServer.readLine();
                JSONObject serverResponse = new JSONObject(textFromServer);

                String value = serverResponse.getString("value");
                String messageType = serverResponse.getString("messageType");

                Platform.runLater(() -> {
                    sent.setText(tfData.getText());
                    received.setText(hexToString(value));
                });

            } catch (IOException e) {
                Platform.runLater(() -> received.setText(""));
                StageHandler.setWarning("Server Connection Failed", e.getMessage());
            }
        }).start();
    }

    private String hexToString(String hex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String hexPair = hex.substring(i, i + 2);
            int decimal = Integer.parseInt(hexPair, 16);
            result.append((char) decimal);
        }
        return result.toString();
    }

    private String stringToHex(String input) {
        StringBuilder hexString = new StringBuilder();
        for (char character : input.toCharArray()) {
            hexString.append(String.format("%02X", (int) character));
        }
        return hexString.toString();
    }

    private void setLayout() {
        HBox fileLayout = new HBox(10);
        fileLayout.setPadding(new Insets(30));

        GridPane inputPane = new GridPane();
        inputPane.setHgap(10);
        inputPane.setVgap(10);

        Label labelMessageType = new Label("Message Type:");
        Label labelData = new Label("Data:");

        String boldStyle = "-fx-font-weight: bold;";
        String underlineStyle = "-fx-underline: true;";

        labelMessageType.setStyle(boldStyle);
        labelData.setStyle(boldStyle);

        HBox buttonLayout = new HBox();
        buttonLayout.setAlignment(Pos.CENTER_RIGHT);
        buttonLayout.getChildren().add(buttonSend);

        inputPane.add(labelMessageType, 0, 0);
        inputPane.add(tfMessageType, 1, 0);
        inputPane.add(labelData, 0, 1);
        inputPane.add(tfData, 1, 1);
        inputPane.add(buttonLayout, 1, 2);

        VBox messageLayout = new VBox(10);

        messageLayout.setPadding(new Insets(0,0,0,50));

        Label labelSentHeader = new Label("Message Sent");
        Label labelReceivedHeader = new Label("Message Received");

        labelSentHeader.setStyle(boldStyle + " " + underlineStyle);
        labelReceivedHeader.setStyle(boldStyle + " " + underlineStyle);

        messageLayout.getChildren().addAll(labelSentHeader, sent, labelReceivedHeader, received);

        fileLayout.getChildren().addAll(inputPane, messageLayout);
        mainLayout.getChildren().add(fileLayout);
    }
}
