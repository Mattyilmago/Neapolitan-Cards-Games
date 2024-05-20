package it.MM.LeTreCarte;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.EncodeException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class JoinLobbyController implements Initializable {

//    private String[] games = {"Scopa", "Briscola", "Tressette", "Somma"};
//    private String[] cards = {"1-B", "10-D", "1-S", "10-C", "1-C", "10-B", "1-D", "10-S"};
//    private int curGame = 0;
//    private boolean isTwoPlayers = true;


    @FXML
    private Button backToMenu;

    @FXML
    private Button joinLobby;

    @FXML
    private TextField roomCode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roomCode.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if(Objects.equals(roomCode.getStyle(), "-fx-border-color: red;")){
                    roomCode.setStyle("");
                }

                if(newValue.length() > 5 || !newValue.matches("\\d*")){
                    roomCode.setText(oldValue);
                }
            }
        });

        roomCode.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                joinLobby.fire();
                roomCode.getParent().requestFocus();
            }
        });

    }


    @FXML
    protected void switchToGamesMenu(ActionEvent event) throws IOException {
        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("GamesMenu.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("GamesMenu.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }



    @FXML
    public void joinLobby(ActionEvent event) throws IOException, DeploymentException, EncodeException, URISyntaxException, InterruptedException {
        //Access to lobby
        SharedData.getGSCInstance().joinRoom(roomCode.getText());

        if(Objects.equals(SharedData.getInstance().getLastError(), "room_not_found")){
            roomCode.clear();
            roomCode.setPromptText("Stanza non trovata");
            roomCode.setStyle("-fx-border-color: red;");
            return;
        }


        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Lobby.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("Table.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


}


