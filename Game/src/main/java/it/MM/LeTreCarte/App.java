package it.MM.LeTreCarte;

import it.MM.LeTreCarte.model.card.Card;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;


public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("StartPage.fxml")));
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
