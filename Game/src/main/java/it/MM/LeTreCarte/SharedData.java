package it.MM.LeTreCarte;

import com.google.gson.JsonObject;
import it.MM.LeTreCarte.model.card.Card;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.Objects;

public class SharedData {
    private static GameServerClient GSCInstance = new GameServerClient();
    private static SharedData instance = new SharedData();
    private final ObservableList<String> lobbyPlayers = FXCollections.observableArrayList();
    private final ObservableList<Card> playerCards = FXCollections.observableArrayList();
    private String selectedGame;
    private String roomCode;
    private final StringProperty roomCodeString;
    private final BooleanProperty isRoomOwner;
    private String playerName;
    private String clientID;
    private ObservableList<JsonObject> moves = FXCollections.observableArrayList();
    private ObservableList<Card> cardsOnTable = FXCollections.observableArrayList();



    private SharedData() {
        this.roomCodeString = new SimpleStringProperty("Caricamento...");
        this.isRoomOwner = new SimpleBooleanProperty(false);
        GSCInstance.initialize();
    }

    public static GameServerClient getGSCInstance() {
        return GSCInstance;
    }

    public static SharedData getInstance() {
        return instance;
    }

    public String getSelectedGame() {
        return selectedGame;
    }

    public void setSelectedGame(String selectedGame) {
        this.selectedGame = selectedGame;
    }

    public StringProperty getRoomCodeString() {
        return roomCodeString;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode){
        this.roomCode = roomCode;
        Platform.runLater(()->{
            if(Objects.equals(roomCode, "-1")){
                this.roomCodeString.setValue("-1");
                return;
            }

            this.roomCodeString.setValue(STR."Codice stanza: \{roomCode}");
        });

    }

    public ObservableBooleanValue isRoomOwner() {
        return isRoomOwner;
    }

    public void setRoomOwner(boolean _isRoomOwner) {
        Platform.runLater(()->{isRoomOwner.setValue(_isRoomOwner);});
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public ObservableList<String> getLobbyPlayers(){
        return this.lobbyPlayers;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public ObservableList<Card> getPlayerCards() {
        return playerCards;
    }

    public void addPlayerCard(Card playerCard) {
        this.playerCards.add(playerCard);
    }

    public void addMove(JsonObject move) {
        this.moves.add(move);
    }

    public ObservableList<JsonObject> getMoves() {
        return moves;
    }

    public void addCardToTable(Card card) {
        this.cardsOnTable.add(card);
    }

    public ObservableList<Card> getCardsOnTable() {
        return cardsOnTable;
    }

}
