package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.Card;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerScopa extends GameManager {
    private final HashMap<Integer, Integer> conversionTablePrimiera; //hashmap per i punti della primiera

    public GameManagerScopa(ArrayList<Player> players) {
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
        this.players = players;
    }


    /**
     * Calculates the points of Primiera for each player
     *
     * @param players
     * @return Player that wins Primiera
     */
    public Player playerWinsPrimiera(ArrayList<Player> players) {
        int[] points = new int[players.size()];

        for (int i = 0; i < players.size(); i++) {
            for (Character s : Card.seeds) {
                points[i] += players.get(i).getHighestValue(conversionTablePrimiera, s);
            }
        }

        System.out.println(players.getFirst().getId() + " Has: " + points[0]);
        System.out.println(players.getLast().getId() + " Has: " + points[1]);
        if (points[0] > points[1]) {
            return players.getFirst();
        } else if (points[0] < points[1]) {
            return players.getLast();
        }
        return null;
    }


    public void calculatePointsScopa(ArrayList<Player> players, int[] scope) {
        for (Player p : players) {
            calculatePointsForPlayer(p, players, scope[players.indexOf(p)]);
        }
    }

    public void calculatePointsForPlayer(Player player, ArrayList<Player> players, int scope) {
        int points = 0;

        //Un punto per chi ha il maggior numero delle carte
        if (player.getDeckPlayer().size() > 20) {
            System.out.println(player.getId() + " wins carte");
            points++;
        }

        //Un punto per chi ha il maggior numero di carte di denari
        if (player.getDeckPlayer().cardsNumberWithSameSeed('D') > 5) {
            System.out.println(player.getId() + " wins denari");
            points++;
        }

        //un punto per chi ha il settebello
        if (player.getDeckPlayer().contains(new Card(7, 'D'))) {
            System.out.println(player.getId() + " wins settebbello");
            points++;
        }

        //un punto per chi vince la primiera
        if (player.equals(playerWinsPrimiera(players))) {
            System.out.println(player.getId() + " wins primiera");
            points++;
        }

        points += scope;
        player.setPoints(player.getPoints() + points);
        System.out.println(player.getId() + " got " + points + " points");
        System.out.println("Now he has " + player.getPoints() + " points\n");
    }
}