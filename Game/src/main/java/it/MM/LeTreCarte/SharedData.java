package it.MM.LeTreCarte;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class SharedData {
    private static GameServerClient GSCInstance = new GameServerClient();
    private static SharedData instance = new SharedData();
    private ObservableList<String> lobbyPlayers = FXCollections.observableArrayList();
    private String selectedGame;
    private String roomCode;
    private String lastError;
    private String playerName;
    private String clientID;



    private SharedData() {GSCInstance.initialize();}

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

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode){
        this.roomCode = roomCode;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String error) {
        this.lastError = error;
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
}
