package it.MM.LeTreCarte;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class LobbyController implements Initializable {

//    private String[] games = {"Scopa", "Briscola", "Tressette", "Somma"};
//    private String[] cards = {"1-B", "10-D", "1-S", "10-C", "1-C", "10-B", "1-D", "10-S"};
//    private int curGame = 0;
//    private boolean isTwoPlayers = true;


    @FXML
    private Button backToMenu;

    @FXML
    private Button startGame;

    @FXML
    private Label gameName;

    @FXML
    private Label roomCode;

    @FXML
    private Button nextGameButton;

    @FXML
    private AnchorPane paneLobby;

    @FXML
    private ListView listPlayers;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //addThisPlayer();
        refreshGameNameLabel();
        refreshRoomCode();

        listPlayers.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        // Controllo se l'elemento corrente è quello che voglio colorare
                        if (SharedData.getInstance().getPlayerName().equals(item)) {
                            // Coloro l'elemento specifico
                            setTextFill(Color.DARKBLUE);
                        } else {
                            // Mantengo il colore predefinito per gli altri elementi
                            setTextFill(Color.BLACK);
                        }

                        // Imposto il testo dell'elemento
                        setText(item);
                    }
                };
            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    addPlayers();
                });
            }
        },0,1000);


    }


    @FXML
    protected void switchToGamesMenu(ActionEvent event) throws IOException {
        SharedData.getInstance().getLobbyPlayers().clear();

        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("it/MM/LeTreCarte/GamesMenu.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("it/MM/LeTreCarte/GamesMenu.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }



    @FXML
    public void switchToTable(ActionEvent event) throws IOException {
        Stage stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("it/MM/LeTreCarte/Table.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("Table.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void refreshGameNameLabel() {
        gameName.setText(SharedData.getInstance().getSelectedGame());
    }

    public void refreshRoomCode() {
        roomCode.setText(STR."Codice stanza: \{SharedData.getInstance().getRoomCode()}");
    }

    public void addPlayers(){
        //System.out.println(SharedData.getInstance().getLobbyPlayers().toString());
        listPlayers.getItems().clear();
        listPlayers.getItems().addAll(SharedData.getInstance().getLobbyPlayers());
        listPlayers.refresh();
    }

}


