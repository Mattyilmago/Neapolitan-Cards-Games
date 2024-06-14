package it.MM.LeTreCarte;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.EncodeException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class GamesController implements Initializable {

    private String[] games = {"Scopa", "Briscola", "Tressette"};
    private String[] cards = {"1-B", "10-D", "1-S", "10-C", "1-C", "10-B", "1-D", "10-S"};
    private int curGame = 0;
    private boolean isTwoPlayers = true;

    @FXML
    private ImageView createImageView;

    @FXML
    private ImageView entryImageView;

    @FXML
    private Label gameName;

    @FXML
    private Button nextGameButton;

    @FXML
    private AnchorPane paneGames;

    @FXML
    private Button playersButton;

    @FXML
    private ImageView playersIcon;

    @FXML
    private Button previousGameButton;

    @FXML
    private Text labelCrea;

    @FXML
    private Text labelEntra;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshGameNameLabel();
        refreshPlayersButton();
    }

    @FXML
    public void changePlayers(ActionEvent event) {
        isTwoPlayers = !isTwoPlayers;
        refreshPlayersButton();
    }

    @FXML
    public void nextGame(ActionEvent event) {
        curGame++;
        refreshGameNameLabel();
    }

    @FXML
    public void previousGame(ActionEvent event) {
        curGame--;
        refreshGameNameLabel();
    }

    @FXML
    public void switchToTable(ActionEvent event) throws IOException {
        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Table.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        //scene.getStylesheets().addCard(getClass().getResource("Table.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

    }

    public void createLobby(javafx.scene.input.MouseEvent mouseEvent) throws IOException, DeploymentException, EncodeException, URISyntaxException, InterruptedException {
        SharedData.getInstance().setSelectedGame(games[curGame]);
        SharedData.getGSCInstance().createRoom();

        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Lobby.fxml")));
        stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        //scene.getStylesheets().addCard(getClass().getResource("Table.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void joinLobby(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        SharedData.getInstance().setSelectedGame(games[curGame]);

        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("joinLobby.fxml")));
        stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        //scene.getStylesheets().addCard(getClass().getResource("Table.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public void refreshGameNameLabel() {
        gameName.setText(games[curGame]);
        refreshPlayersButton();
        refreshCardsImage();

        if (curGame == 0) previousGameButton.setVisible(false);
        else previousGameButton.setVisible(true);

        if (curGame == games.length - 1) nextGameButton.setVisible(false);
        else nextGameButton.setVisible(true);
    }
    public void refreshPlayersButton() {
        if (isTwoPlayers) {
            playersButton.setText("2 GIOCATORI");
            playersIcon.setImage(new Image(getClass().getResource("due-dita.png").toExternalForm()));
            playersButton.setStyle("-fx-background-color: #A92222");
        } else {
            playersButton.setText("4 GIOCATORI");
            playersIcon.setImage(new Image(getClass().getResource("quattro-dita.png").toExternalForm()));
            playersButton.setStyle("-fx-background-color: #521A6A");
        }
    }

    public void refreshCardsImage(){
        String create = cards[curGame * 2] + ".png";
        String entry = cards[curGame * 2 + 1] + ".png";

        createImageView.setImage(new Image(getClass().getResource("Cards_png/" + create).toExternalForm()));
        entryImageView.setImage(new Image(getClass().getResource("Cards_png/" + entry).toExternalForm()));
    }
}


