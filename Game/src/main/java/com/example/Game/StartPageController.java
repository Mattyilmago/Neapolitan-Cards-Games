package com.example.Game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartPageController implements Initializable {
    @FXML
    private ImageView imageView;
    @FXML
    private Button button;
    @FXML
    private Text text;

    @FXML
    protected void switchToScopa(ActionEvent event) throws IOException {
        Stage stage;
        Parent root = FXMLLoader.load(getClass().getResource("Scopa.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Scopa.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void setResponsive(ImageView imageView) {
        Dimension size = Toolkit. getDefaultToolkit(). getScreenSize();
        Dimension newSize = new Dimension();
        newSize.setSize(size.height * imageView.getFitWidth() / imageView.getFitHeight(), size.height);
        this.imageView.setFitHeight(newSize.getHeight());
        this.imageView.setFitWidth(newSize.getWidth());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setResponsive(imageView);
    }
}
