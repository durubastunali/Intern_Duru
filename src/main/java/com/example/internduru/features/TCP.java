package com.example.internduru.features;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketAddress;

public class TCP {

    private VBox mainLayout;
    private Button buttonSend = new Button("Send");

    private int serverPort = 1234;
    private Socket socket = null;
    private TextField tfMessageType = new TextField();
    private TextField tfData = new TextField();
    private Label sent = new Label("NONE");
    private Label received = new Label("NONE");


    public TCP(VBox mainLayout) {
        this.mainLayout = mainLayout;
        startConnection();
    }

    private void startConnection() {
        setLayout();

        buttonSend.setOnAction(event -> {
            connect();
        });
    }

    private void connect() {
        new Thread(() -> {
            try {
                socket = new Socket("192.168.50.154", serverPort);
                BufferedReader bufferedReaderInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter outputClient = new PrintWriter(socket.getOutputStream(), true);

                // Example message to send; you can replace this with input from a text field
                String messageToSend = tfData.getText();
                outputClient.println(messageToSend);

                String textFromServer = bufferedReaderInput.readLine();
                Platform.runLater(() -> {
                    sent.setText(tfData.getText());
                    received.setText(textFromServer);
                });

            } catch (IOException e) {
                Platform.runLater(() -> received.setText(""));
                StageHandler.setWarning("Server Connection Failed", e.getMessage());
            }
        }).start();
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
