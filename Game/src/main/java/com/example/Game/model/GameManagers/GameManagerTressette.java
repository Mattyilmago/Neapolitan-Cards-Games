package com.example.Game.model.GameManagers;

import com.example.Game.Player;
import com.example.Game.model.card.Card;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerTressette extends GameManager {
    private final HashMap<Integer, Integer> conversionTableTressette; //hashmap per i punti di tressette

    public GameManagerTressette(ArrayList<Player> players) {
        conversionTableTressette = new HashMap<>();
        conversionTableTressette.put(1, 3);
        conversionTableTressette.put(2, 1);
        conversionTableTressette.put(3, 1);
        conversionTableTressette.put(8, 1);
        conversionTableTressette.put(9, 1);
        conversionTableTressette.put(10, 1);
        this.players = players;
    }

    public void calculatePoints(ArrayList<Player> players, boolean[] lastCard) {
        for (Player p : players) {
            calculatePointsForPlayer(p, lastCard[players.indexOf(p)]);
        }
    }

    /**
     * Calculate the points of Tressette for one player
     *
     * @param player Player to calculate points
     * @return Points won of the player
     */
    public void calculatePointsForPlayer(Player player, boolean lastCard) { //lastCard vale 1 se il giocatore ha fatto l'ultima presa, 0 altrimenti
        int points = 0;
        for (Card c : player.getDeckPlayer().getCards()) {
            if (conversionTableTressette.containsKey(c.getValue())) {
                points += conversionTableTressette.get(c.getValue());
            }
        }

        points /= 3;
        if (lastCard) {
            points++;
        }
        player.setPoints(player.getPoints() + points);
        System.out.println(player.getId() + " got " + points + " points");
        System.out.println("Now he has " + player.getPoints() + " points\n");
    }
}
