module com.example.Game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.Game to javafx.fxml;
    exports com.example.Game;
    exports com.example.Game.model.GameManagers;
    opens com.example.Game.model.GameManagers to javafx.fxml;
    exports com.example.Game.model.card;
    opens com.example.Game.model.card to javafx.fxml;
}