package com.example.Game.model.GameManagers;

import com.example.Game.Player;

import java.util.ArrayList;

public abstract class GameManager {
    public ArrayList<Player> players;

    public void calculatePoints(ArrayList<Player> players) {}

    public void calculatePointsForPlayer(Player player){}


}
