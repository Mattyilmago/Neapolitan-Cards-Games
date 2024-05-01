module com.example.Game {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.Game to javafx.fxml;
    exports com.example.Game;
}