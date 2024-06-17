package it.MM.LeTreCarte;

import it.MM.LeTreCarte.model.card.Card;
import jakarta.websocket.EncodeException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
    private ImageView leftArrow;

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
        leftArrow.setImage(new Image(getClass().getResource("left_arrow.png").toExternalForm()));

        gameName.setText(SharedData.getInstance().getSelectedGame());

        roomCode.textProperty().bind(SharedData.getInstance().getRoomCodeString());

        SharedData.getInstance().isRoomOwner().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                startGame.setDisable(!t1);
            }
        });


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
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Fxml/GamesMenu.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Css/GamesMenu.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }



    @FXML
    public void switchToTable(ActionEvent event) throws IOException, EncodeException {
        SharedData.getGSCInstance().requestCards(Objects.equals(SharedData.getInstance().getSelectedGame(), "Tressette") ? 10 : 3);

        startGame.setDisable(true);

        Task<Void> waitForCards = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                //TODO modificare il while creando un hashmap <String, int> con nome_gioco e numero_carte
                while (SharedData.getInstance().getPlayerCards().size() != (SharedData.getInstance().getSelectedGame().equals("Tressette") ? 10 : 3) && SharedData.getInstance().getCardsOnTable().size() != (SharedData.getInstance().getSelectedGame().equals("Scopa") ? 4 :0)) {
                    System.out.println("cards: " + SharedData.getInstance().getPlayerCards().size());
                    Thread.sleep(500);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                // Esegui l'aggiornamento dell'interfaccia utente sul thread dell'interfaccia utente
                Platform.runLater(() -> {
                    try {
                        Stage stage;
                        Parent root = null;
                        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Fxml/Table.fxml")));
                        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("Css/Table.css").toExternalForm());
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        };

        new Thread(waitForCards).start();


    }

    public void addPlayers(){
        //System.out.println(SharedData.getInstance().getLobbyPlayers().toString());
        listPlayers.getItems().clear();
        listPlayers.getItems().addAll(SharedData.getInstance().getLobbyPlayers());
        listPlayers.refresh();
    }

}


