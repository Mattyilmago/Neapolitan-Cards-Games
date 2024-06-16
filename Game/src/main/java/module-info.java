module it.MM.LeTreCarte {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires junit;
    requires jdk.jsobject;
    requires com.google.gson;
    requires grizzly.http.server;
    requires jakarta.websocket.client;
    requires org.glassfish.tyrus.core;
    requires javafx.graphics;


    opens it.MM.LeTreCarte to javafx.fxml;
    exports it.MM.LeTreCarte;
    exports it.MM.LeTreCarte.model.GameManagers;
    opens it.MM.LeTreCarte.model.GameManagers to javafx.fxml;
    exports it.MM.LeTreCarte.model.card;
    opens it.MM.LeTreCarte.model.card to javafx.fxml;
}