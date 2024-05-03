package com.example.Game.model.GameManagers;

import com.example.Game.Player;
import com.example.Game.model.card.Card;
import com.example.Game.model.card.cardcontainer.Deck;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerScopa extends GameManager {
    private final HashMap<Integer, Integer> conversionTablePrimiera; //hashmap per i punti della primiera

    public GameManagerScopa() {
        conversionTablePrimiera = new HashMap<>();
        conversionTablePrimiera.put(7, 21);
        conversionTablePrimiera.put(6, 18);
        conversionTablePrimiera.put(1, 16);
        conversionTablePrimiera.put(5, 15);
        conversionTablePrimiera.put(4, 14);
        conversionTablePrimiera.put(3, 13);
        conversionTablePrimiera.put(2, 12);
        conversionTablePrimiera.put(8, 10);
        conversionTablePrimiera.put(9, 10);
        conversionTablePrimiera.put(10, 10);
    }


    /**
     * Calculates the points of Primiera for each player
     * @param players
     * @return Player that wins Primiera
     */
    public Player playerWinsPrimiera(ArrayList<Player> players) {
        int[] points = new int[players.size()];

        for (int i = 0; i < players.size(); i++) {
            for (Character s : Deck.seeds) {
                 points[i] += players.get(i).getHighestValue(conversionTablePrimiera, s);
            }
        }

        if(points[0] > points[1]) {
            return players.getFirst();
        }
        return players.getLast();
    }

    /**
     * Calculates the points of Scopa for one player
     * @param player Player to calculate the points
     * @param players Arraylist of the players
     * @return Points won of the player
     */
    @Override
    public int calculatePoints(Player player, ArrayList<Player> players) {
        int points = 0;

        //Un punto per chi ha il maggior numero delle carte
        if (player.getDeckPlayer().size() > 20) {
            points++;
        }

        //Un punto per chi ha il maggior numero di carte di denari
        if (player.getDeckPlayer().cardsWithSameSeed('D') > 5) {
            points++;
        }

        //un punto per chi ha il settebello
        if (player.getDeckPlayer().contains(new Card(7, 'D'))) {
            points++;
        }

        //un punto per chi vince la primiera
        if (player.equals(playerWinsPrimiera(players))) {
            points++;
        }
        return points;
    }
}