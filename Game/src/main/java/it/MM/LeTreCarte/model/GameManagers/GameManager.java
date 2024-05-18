package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;

import java.util.ArrayList;

public abstract class GameManager {
    public ArrayList<Player> players;

    public void calculatePoints(ArrayList<Player> players) {}

    public void calculatePointsForPlayer(Player player){}


}
