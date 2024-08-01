package com.example.internduru.features;

import com.example.internduru.StageHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RZ {

    private final VBox mainLayout;
    private String urunAdi;
    private String odemeTipi;
    private String fiscalNo;
    private String logoLines = "";
    private int fisNo;
    private int ekuNo;
    private int zNo;
    private int maliFisSayisi;
    private int satisFisiSayisi;
    private int maliRaporSayisi;
    private double toplamFisTutari;
    private double kdvOrani;
    private double nakitTutari;
    private double kasaNakitTutari;
    private double eftTutari;
    private double kasaKrediTutari;
    private Date tarih;
    private LocalTime saat;
    private final List<Path> files = new ArrayList<>();


    public RZ(VBox mainLayout) {
        this.mainLayout = mainLayout;
        setLayout();
    }

    public void setLayout() {
        VBox fileLayout = new VBox();
        ComboBox<String> fileComboBox = new ComboBox<>();
        StageHandler.setFileLayout(mainLayout, fileLayout, fileComboBox, "R - Z");
        loadFiles(fileComboBox, fileLayout);
    }

    private void loadFiles(ComboBox<String> fileComboBox, VBox fileLayout) {
        Path resourcesDirectory = Paths.get("resources");
        String path = resourcesDirectory + "\\ej\\z";

        try {
            Files.walk(Paths.get(path)) //directoryde ve alt directorylerinde gez
                    .filter(Files::isRegularFile) //klasörleri seçmeden sadece dosyaları seç
                    .forEach(files::add); //bulduğu her dosyayı files'a atsın
            if (!files.isEmpty()) {
                files.forEach(file -> fileComboBox.getItems().add(file.toString().substring(path.length() + 1))); //her bir dosyanın adını combobox'a ekle
                fileComboBox.setOnAction(event -> {
                    if (fileComboBox.getValue() != null) {
                        openFileInfo(path+ "\\" + fileComboBox.getValue(), fileLayout);
                    }
                });
            }
        } catch (IOException e) {
            StageHandler.setWarning("Klasöre erişim sağlanamadı", e.getMessage());
        }
    }

    private void openFileInfo(String filePath, VBox fileLayout) {
        logoLines = "";
        fileLayout.getChildren().clear();
        String fileName = filePath.substring(filePath.length() - 5);
        fileLayout.setPadding(new Insets(20));
        if (readFile(filePath)) {
            fileLayout.getChildren().addAll(createLogoLinesBox(fileName), createInfoBox(fileName));
        }
    }

    private HBox createInfoBox(String input) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        HBox hbox = new HBox(10);
        Label headers = new Label();
        Label info = new Label();
        if (input.charAt(0) == 'r') {
            headers = new Label("""
                    Tarih:
                    Saat:
                    Fiş No:
                    Ürün Adı:
                    KDV Oranı:
                    Toplam Fiş Tutarı:
                    Ödeme Tipi:
                    Ekü No:
                    Z No:
                    Fiscal No:
                    """);
            info = new Label(outputFormat.format(tarih) + "\n" + saat + "\n" + fisNo + "\n" + urunAdi + "\n" +
                    kdvOrani + "\n" + toplamFisTutari + "\n" + odemeTipi + "\n" + ekuNo + "\n" + zNo + "\n" + fiscalNo);
            headers.setPrefWidth(110);
        } else if (input.charAt(0) == 'z') {
            headers = new Label("""
                    Tarih:
                    Saat:
                    Fiş No:
                    Mali Fiş Sayısı:
                    Satış Fişi Sayısı
                    MaliRaporSayısı
                    Nakit Ödeme Tutarı:
                    Kasa Nakit Ödeme Tutarı:
                    EFT-POS Ödeme Tutarı:
                    Kasa Kredi Ödeme Tutarı:
                    Toplam Kasa Ödeme Tutarı:
                    Toplam Ödeme Tutarı:
                    Ekü No:
                    Z No:
                    Fiscal No:
                    """);
            info = new Label(outputFormat.format(tarih) + "\n" + saat + "\n" + fisNo + "\n" + maliFisSayisi + "\n" +
                    satisFisiSayisi + "\n" + maliRaporSayisi + "\n" + nakitTutari + "\n" + kasaNakitTutari + "\n" +
                    eftTutari + "\n" + kasaKrediTutari + "\n" + toplamFisTutari + "\n" + toplamFisTutari + "\n" +
                    ekuNo + "\n" + zNo + "\n" + fiscalNo);
            headers.setPrefWidth(180);
        }
        headers.setStyle("-fx-font-weight: bold;");
        hbox.getChildren().addAll(headers, info);
        return hbox;
    }

    private HBox createLogoLinesBox(String input) {
        Label header_ll = new Label("Logo Lines:");
        if (input.charAt(0) == 'r') {
            header_ll.setPrefWidth(110);
        } else if (input.charAt(0) == 'z') {
            header_ll.setPrefWidth(180);
        }
        header_ll.setStyle("-fx-font-weight: bold;");
        Label label_ll = new Label(logoLines);
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(header_ll, label_ll);
        return hbox;
    }

    private boolean readFile(String path) {
        String userInput = path.substring(path.length() - 5);
        File file = new File(path);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "Windows-1254"))) { //Encoding = ANSI, Windows-1254
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                if (userInput.charAt(0) == 'r') {
                    parseRFile(content.toString());
                } else if (userInput.charAt(0) == 'z') {
                    parseZFile(content.toString());
                }
                return true;
            } catch (IOException e) {
                StageHandler.setWarning("Dosyaya erişim sağlanamadı", e.getMessage());
            }
        } return false;
    }

    private void setCommonHeaders(String fileContent) {
        int endOfLogoLine = 0;
        String[] lines = fileContent.split("\n");
        for (int i = 0; i < lines.length; i++) {
            logoLines += lines[i].substring(3) + "\n";
            if (lines[i + 2].contains("SAAT")) {
                endOfLogoLine = i;
                break;
            }
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            tarih = inputFormat.parse(lines[endOfLogoLine + 1].substring(3, 13));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseRFile(String fileContent) {
        setCommonHeaders(fileContent);
        String[] lines = fileContent.split("\n");
        String index;
        String nextIndex;
        String value;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (String line : lines) {
            String[] splitLines = line.split("\\s+");
            for (int j = 0; j < splitLines.length; j++) {
                index = splitLines[j];
                if (j + 2 < splitLines.length) {
                    nextIndex = index + " " + splitLines[j + 1];
                    value = splitLines[j + 2];
                    switch (nextIndex) {
                        case ("FİŞ NO:") -> fisNo = returnParseInt(value);
                        case ("111EKÜ NO:") -> ekuNo = returnParseInt(value);
                        case ("Z NO:") -> zNo = returnParseInt(value);
                    }
                } if (j + 1 < splitLines.length) {
                    value = splitLines[j + 1];
                    switch (index) {
                        case ("122TOP") -> toplamFisTutari = Double.parseDouble(value.substring(1).replace(',', '.'));
                        case ("111SAAT") -> saat = LocalTime.parse(value.substring(1), formatter);
                        case ("111uF") -> fiscalNo = value.substring(2);
                    }
                } if (index.contains("%") && j - 1 >= 0) {
                    urunAdi = splitLines[j - 1].substring(3);
                    kdvOrani = Double.parseDouble(index.substring(1));
                }
                switch (index) {
                    case ("111NAKİT") -> odemeTipi = "NAKİT";
                    case ("111EFT-POS") -> odemeTipi = "EFT-POS";
                }
            }
        }
    }

    private void parseZFile(String fileContent) {
        setCommonHeaders(fileContent);
        String[] lines = fileContent.split("\n");
        String[] splitLines;
        String[] splitLinesNext;
        String index;
        String nextIndex;
        String key;
        String value = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < lines.length; i++) {
            splitLines = lines[i].split("\\s+");
            for (int j = 0; j < splitLines.length; j++) {
                index = splitLines[j];
                if (i + 1 < lines.length) {
                    splitLinesNext = lines[i + 1].split("\\s+");
                    if (j + 1 < splitLines.length && splitLinesNext.length > 2) {
                        nextIndex = splitLines[j + 1];
                        value = splitLinesNext[2];
                        key = index + " " + nextIndex;
                        switch (key) {
                            case ("111NAKİT TAHSİLAT") -> nakitTutari = Double.parseDouble(value.substring(1));
                            case ("111**KASA NAKİT**") -> kasaNakitTutari = Double.parseDouble(value.substring(1));
                            case ("KASA KREDİ") -> kasaKrediTutari = Double.parseDouble(value.substring(1));
                        }
                    } else if (index.equals("111EFTPOS")) {
                        eftTutari = Double.parseDouble(value.substring(1));
                    }
                } if (j + 1 < splitLines.length) {
                    nextIndex = splitLines[j + 1];
                    switch (index) {
                        case ("111SAAT") -> saat = LocalTime.parse(nextIndex.substring(1), formatter);
                        case ("111uF") -> fiscalNo = nextIndex.substring(2);
                        case ("122TOP") -> toplamFisTutari = Double.parseDouble((nextIndex.replace(".", "")).replace(",", "."));
                    }
                } if (j + 2 < splitLines.length) {
                    nextIndex = splitLines[j + 1];
                    key = index + " " + nextIndex;
                    value = splitLines[j + 2];
                    switch (key) {
                        case ("FİŞ NO:") -> fisNo = returnParseInt(value);
                        case ("111EKÜ NO:") -> ekuNo = returnParseInt(value);
                        case ("Z NO:") -> zNo = returnParseInt(value);
                        case ("-SATIŞ FİŞLERİ") -> satisFisiSayisi = Integer.parseInt(value);
                        case ("-MALİ RAPORLAR") -> maliRaporSayisi = Integer.parseInt(value);
                    }
                } if (j + 3 < splitLines.length && (splitLines[j] + " " + splitLines[j + 1] + " " + splitLines[j + 2]).contains("MALİ FİŞ SAYISI")) {
                    maliFisSayisi = Integer.parseInt(splitLines[j + 3]);

                }
            }
        }
    }

    private int returnParseInt(String line) {
        return Integer.parseInt(line.replaceAll("\\D", ""));
    }
}
