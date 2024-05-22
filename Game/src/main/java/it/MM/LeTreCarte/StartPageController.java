package it.MM.LeTreCarte;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;



import java.io.IOException;
import java.util.Objects;

public class StartPageController{
    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button mainButton;

    @FXML
    protected void switchToGamesMenu(ActionEvent event) throws IOException {
        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("/it/MM/LeTreCarte/GamesMenu.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("GamesMenu.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

   /* @FXML
   protected void setResponsive(ImageView imageView) {
        Dimension size = Toolkit. getDefaultToolkit(). getScreenSize();
        Dimension newSize = new Dimension();
        newSize.setSize(size.height * imageView.getFitWidth() / imageView.getFitHeight(), size.height);
        this.imageView.setFitHeight(newSize.getHeight());
        this.imageView.setFitWidth(newSize.getWidth());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }*/
}

