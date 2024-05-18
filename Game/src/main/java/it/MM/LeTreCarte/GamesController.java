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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class GamesController implements Initializable {

    private String[] games = {"Scopa", "Briscola", "Tressette", "Somma"};
    private String[] cards = {"1-B", "10-D", "1-S", "10-C", "1-C", "10-B", "1-D", "10-S"};
    private int curGame = 0;
    private boolean isTwoPlayers = true;

    @FXML
    private ImageView cardCreate;

    @FXML
    private ImageView cardEntry;

    @FXML
    private Button createButton;

    @FXML
    private Button entryButton;

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
//    @FXML
//    public void switchToTable(ActionEvent event) throws IOException {
//        Stage stage;
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Table.fxml")));
//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        Scene scene = new Scene(root);
//        //scene.getStylesheets().add(getClass().getResource("Table.css").toExternalForm());
//        stage.setScene(scene);
//        stage.show();
//    }

    @FXML
    public void createLobby(ActionEvent event) throws URISyntaxException, IOException, InterruptedException, DeploymentException, EncodeException {
        SharedData.getInstance().setSelectedGame(games[curGame]);
        SharedData.getGSCInstance().createRoom();
        switchToLobby(event);
    }

    @FXML
    public void joinLobby(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
        SharedData.getInstance().setSelectedGame(games[curGame]);

        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("joinLobby.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("Table.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private void switchToLobby(ActionEvent event) throws IOException {
        //Save selected game
        //SharedData.getInstance().setSelectedGame(games[curGame]);


        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Lobby.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("Table.css").toExternalForm());

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
            playersButton.setText("2 giocatori");
            playersIcon.setImage(new Image(getClass().getResource("due-dita.png").toExternalForm()));
            playersButton.setStyle("-fx-background-color: #A92222");
        } else {
            playersButton.setText("4 giocatori");
            playersIcon.setImage(new Image(getClass().getResource("quattro-dita.png").toExternalForm()));
            playersButton.setStyle("-fx-background-color: #521A6A");
        }
    }
    public void refreshCardsImage(){
        String create = cards[curGame * 2] + ".jpg";
        String entry = cards[curGame * 2 + 1] + ".jpg";

        cardCreate.setImage(new Image(getClass().getResource("Cards_jpg/" + create).toExternalForm()));
        cardEntry.setImage(new Image(getClass().getResource("Cards_jpg/" + entry).toExternalForm()));
    }
}


