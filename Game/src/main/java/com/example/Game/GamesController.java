package com.example.Game;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GamesController implements Initializable {

    @FXML
    private Label gameName;
    private String[] games = {"Scopa","Briscola","Lorenzo","Somma"};
    private int curGame = 0;

    @FXML
    private Button nextGameButton;

    @FXML
    private Button previousGameButton;

    @FXML
    private ToggleButton playersButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshGameNameLabel();
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

    public void refreshGameNameLabel() {
        gameName.setText(games[curGame]);

        if (curGame == 0) previousGameButton.setVisible(false);
        else previousGameButton.setVisible(true);

        if (curGame == games.length - 1) nextGameButton.setVisible(false);
        else nextGameButton.setVisible(true);
    }
}
