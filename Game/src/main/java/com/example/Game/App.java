package com.example.Game;

import com.example.Game.model.GameManagers.*;
import com.example.Game.model.card.cardcontainer.Deck;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("StartPage.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Le Tre Carte");
        stage.setScene(scene);
        stage.show();
    }

    public void setResponsive(){
        
    }
}
