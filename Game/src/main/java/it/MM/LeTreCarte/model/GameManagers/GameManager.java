package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.cardcontainer.Table;

import java.util.ArrayList;

public abstract class GameManager {
    public Table table;

    public void calculatePoints(ArrayList<Player> players) {}

    public void calculatePointsForPlayer(Player player){}


}
