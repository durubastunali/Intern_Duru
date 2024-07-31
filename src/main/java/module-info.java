module com.example.internduru {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.desktop;
        requires org.json;
        requires java.sql;


        opens com.example.internduru to javafx.fxml;
        exports com.example.internduru;
        exports com.example.internduru.Database;
        opens com.example.internduru.Database to javafx.fxml;
        exports com.example.internduru.Features;
        opens com.example.internduru.Features to javafx.fxml;
        }