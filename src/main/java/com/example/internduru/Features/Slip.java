package com.example.internduru.Features;

import com.example.internduru.Database.DatabaseConnector;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Slip {

    private final VBox mainLayout;
    private final ArrayList<HBox> labels = new ArrayList<>();
    private final ArrayList<Path> slipFiles = new ArrayList<>();

    public Slip(VBox mainLayout) {
        this.mainLayout = mainLayout;
        setLayout();
    }

    private void setLayout() {
        VBox fileLayout = new VBox();
        ComboBox<String> fileComboBox = new ComboBox<>();
        StageHandler.setFileLayout(mainLayout, fileLayout, fileComboBox, "Slip");
        loadSlipFiles(fileComboBox, fileLayout);
    }

    private void loadSlipFiles(ComboBox<String> fileComboBox, VBox fileLayout) {
        Path resourcesDirectory = Paths.get("resources");
        String path = resourcesDirectory + "\\ej\\slips";

        try {
            Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile) //Klasörleri seçmeden sadece dosyaları al
                    .filter(file -> file.toString().endsWith(".json")) //Aldığı dosyaların uzantısı .json olsun
                    .forEach(slipFiles::add); //Filtreden geçen tüm dosyaları slipFiles listesine ekle
            if (!slipFiles.isEmpty()) {
                slipFiles.forEach(file -> {
                    String fileName = file.toString().substring(path.length() + 1);
                    fileComboBox.getItems().add(fileName);
                });
                fileComboBox.setOnAction(event -> {
                    if (fileComboBox.getValue() != null) {
                        try {
                            readSlip(path + "\\" + fileComboBox.getValue(), fileLayout);
                        } catch (JSONException | IOException e) {
                            StageHandler.setWarning("JSON dosyası açılamadı.", e.getMessage());
                        }
                    }
                });
            }
        } catch (IOException e) {
            StageHandler.setWarning("Klasöre erişim sağlanamadı", e.getMessage());
        }
    }

    //Kullanılmayan yorumdaki değerler tüm dosyalarda 0
    private void readSlip(String inputPath, VBox fileLayout) throws JSONException, IOException {
        fileLayout.getChildren().clear();

        //UTF-8 diye belirtmeden Gradle'a geçince UTF-8 algılamadı?
        String content;
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(inputPath), "UTF-8");
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            content = sb.toString();
        }

        JSONArray slip = new JSONArray(content);
        String align = null, font = null, style = null, type, value;
        int height = 0, width = 0, level, lineHeight, offset;
        boolean lineFeed = false;
        for (int i = 0; i < slip.length(); i++) {
            JSONObject item = slip.getJSONObject(i);
            type = item.getString("type");
            value = item.getString("value");
            if (!type.equals("PAPERSKIP")) {
                JSONObject attribute = item.getJSONObject("attr");
                align = attribute.getString("align");
                height = attribute.getInt("height");
                level = attribute.getInt("level");
                lineFeed = attribute.getBoolean("lineFeed");
                lineHeight = attribute.getInt("lineHeight");
                offset = attribute.getInt("offset");
                width = attribute.getInt("width");
                if (!type.equals("IMAGE")) {
                    font = attribute.getString("font");
                    style = attribute.getString("style");
                }
            }
            printSlip(align, type, value, lineFeed, width, height, font, style, fileLayout);
        }
    }


    private void printSlip(String align, String type, String value, boolean lineFeed, int width, int height, String font, String style, VBox root) {
        Font smallFont = new Font(12); //3x
        Font middleFont = new Font(16); //4x
        Font largeFont = new Font(20); //5x

        HBox labelBox = new HBox(10);
        labelBox.setPrefWidth(270);

        HBox imageBox = new HBox();
        imageBox.setMaxWidth(270);
        imageBox.setPadding(new Insets(10));

        switch (type) {
            case "TEXT" -> {
                Label label = new Label(value.trim());
                switch (font) {
                    case "small" -> label.setFont(smallFont);
                    case "normal" -> label.setFont(middleFont);
                    case "large" -> label.setFont(largeFont);
                }
                if (style.equals("bold")) {
                    label.setStyle("-fx-font-weight: bold;");
                }
                switch (align) {
                    case "center" -> labelBox.setAlignment(Pos.CENTER);
                    case "right" -> labelBox.setAlignment(Pos.CENTER_RIGHT);
                    case "left" -> labelBox.setAlignment(Pos.CENTER_LEFT);
                }
                labelBox.getChildren().add(label);
                if (lineFeed) {
                    HBox hBox = new HBox(10);
                    labels.add(labelBox);
                    for (int i = labels.size() - 1; i >= 0; i--) {
                        if (labels.size() > 1) {
                            labels.get(i).setPrefWidth(135);
                        }
                        hBox.getChildren().add(labels.get(i));
                    }
                    root.getChildren().add(hBox);
                    labels.clear();
                } else {
                    labels.add(labelBox);
                }
            }
            case "PAPERSKIP" -> {
                Label label = new Label("");
                root.getChildren().add(label);
            }
            case "IMAGE" -> {
                try {
                    byte[] decodedBytes = hexStringToByteArray(value);
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));
                    Image image = bufferedImageToImage(bufferedImage);
                    ImageView imageView = new ImageView(image);

                    imageView.setFitHeight(height);
                    imageView.setFitWidth(width);
                    imageBox.setAlignment(Pos.CENTER);
                    imageBox.getChildren().add(imageView);
                    root.getChildren().add(imageBox);
                } catch (IOException e) {
                    StageHandler.setWarning("Slip logosu oluşturulamadı", e.getMessage());
                }
            }
        }
    }

    private byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private Image bufferedImageToImage(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return new Image(byteArrayInputStream);
    }
}
