package com.example.Game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;


public class App extends Application {

    public static void main(String[] args) throws IOException, InterruptedException {

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GameServerClient GSC = new GameServerClient();
        GameServerFetcher GSF = new GameServerFetcher(GSC);

        GSC.register();
        GSC.createRoom("vediamoSeParte");
        GSF.setDaemon(true);
        //GSF.start();


        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("StartPage.fxml"));
        }
        catch (Exception e) { throw new RuntimeException(e); }
        Scene scene = new Scene(root);
        stage.setTitle("Le Tre Carte");
        stage.setResizable(false);
        scene.getStylesheets().add(getClass().getResource("StartPage.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
