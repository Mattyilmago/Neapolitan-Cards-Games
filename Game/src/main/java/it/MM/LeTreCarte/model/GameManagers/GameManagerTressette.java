package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Table;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerTressette {
    private static final HashMap<Integer, Integer> cardsPoints = new HashMap<>(){{
        put(1, 3);
        put(2, 1);
        put(3, 1);
        put(8, 1);
        put(9, 1);
        put(10, 1);
    }}; //hashmap per i punti di tressette
    private static final HashMap<Integer, Integer> cardsRank = new HashMap<>(){{
        put(3, 10);
        put(2, 9);
        put(1, 8);
        put(10, 7);
        put(9, 6);
        put(8, 5);
        put(7, 4);
        put(6, 3);
        put(5, 2);
        put(4, 1);
    }}; //hashmap per i gradi delle carte di Briscola
    public static Boolean[] lastCardArray = {false, false};

    /**
     * Calculate who wins this turn and move table's cards to winner's deckplayer
     */
    public static Player calculateWinnerTurn(Table table){
        int indexWinner = 0;
        Character seed = table.getCards().getFirst().getSeed();

        for(Card card : table.getCards()){
            if(card.getSeed() == seed && cardsRank.get(card.getValue()) > cardsRank.get(table.getCards().get(indexWinner).getValue())){
                indexWinner = table.getCards().indexOf(card);
            }
        }

        System.out.println("Winner is " + table.getPlayers().get(indexWinner).getId() + " with " + table.getCards().get(indexWinner));
        return table.getPlayers().get(indexWinner);
    }

    public static void calculatePoints(Table table) {
        for (Player p : table.getTeams()) {
            calculatePointsForPlayer(p, lastCardArray[table.getTeams().indexOf(p)]);
        }

        lastCardArray[0] = false;
        lastCardArray[1] = false;
    }

    /**
     * Calculate the points of Tressette for one player
     *
     * @param player Player to calculate points
     * @return Points won of the player
     */
    public static void calculatePointsForPlayer(Player player, boolean lastCard) { //lastCard vale 1 se il giocatore ha fatto l'ultima presa, 0 altrimenti
        int points = 0;
        for (Card c : player.getDeckPlayer().getCards()) {
            if (cardsPoints.containsKey(c.getValue())) {
                points += cardsPoints.get(c.getValue());
            }
        }

        points /= 3;
        if (lastCard) {
            points++;
        }
        player.setPoints(player.getPoints() + points);
        System.out.println(player.getId() + " got " + points + " points");
        System.out.println("Now he has " + player.getPoints() + " points");
    }
}
