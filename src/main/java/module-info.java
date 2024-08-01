module com.example.internduru {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.desktop;
        requires org.json;
        requires java.sql;


        opens com.example.internduru to javafx.fxml;
        exports com.example.internduru;
        exports com.example.internduru.database;
        opens com.example.internduru.database to javafx.fxml;
        exports com.example.internduru.features;
        opens com.example.internduru.features to javafx.fxml;
        }