module com.example.snake {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.Fritz to javafx.fxml;
    exports com.Fritz;
}