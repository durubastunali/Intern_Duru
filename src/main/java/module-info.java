module com.example.internduru {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.internduru to javafx.fxml;
    exports com.example.internduru;
}