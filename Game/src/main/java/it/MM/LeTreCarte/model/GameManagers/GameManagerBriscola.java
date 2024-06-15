package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Table;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerBriscola {
    //hashmap per i punti di Briscola
    private static final HashMap<Integer, Integer> cardsPoints = new HashMap<>(){{
        put(1, 11);
        put(3, 10);
        put(10, 4);
        put(9, 3);
        put(8, 2);
    }};
    //hashmap per i gradi delle carte di Briscola
    private static final HashMap<Integer, Integer> cardsRank = new HashMap<>(){{
        put(1,10);
        put(3,9);
        put(10, 8);
        put(9, 7);
        put(8, 6);
        put(7, 5);
        put(6, 4);
        put(5, 3);
        put(4, 2);
        put(2, 1);
    }};

    /**
     * Calculate who wins this turn and move table's cards to winner's deckplayer
     * @param table
     * @param briscola
     */
    public static Player calculateWinnerTurn(Table table, Character briscola) {
        boolean isWinnerBriscola = false;
        int indexWinner = 0;
        char seed = table.getCards().getFirst().getSeed();

        for(Card card : table.getCards()) {
            if(isWinnerBriscola) { //Se la carta che vince è una briscola e se trovo un'altra briscola di punteggio maggiore aggiorno il vincitore
                if(card.getSeed() == briscola && cardsRank.get(card.getValue()) > cardsRank.get(table.getCards().get(indexWinner).getValue())) {
                    indexWinner = table.getCards().indexOf(card);
                }
            }

            //Se la carta che vince non è una briscola ma trovo una carta di valore maggiore con lo stesso seme o una carta di briscola aggiorno il vincitore
            else if(card.getSeed() == seed && cardsRank.get(card.getValue()) > cardsRank.get(table.getCards().get(indexWinner).getValue())) {
                if(card.getSeed() == briscola) {
                    isWinnerBriscola = true;
                }
                indexWinner = table.getCards().indexOf(card);
            }

            //Se la carta che vince non è una briscola ma trovo una briscola allora aggiorno il vincitore
            else if(card.getSeed() == briscola){
                isWinnerBriscola = true;
                indexWinner = table.getCards().indexOf(card);
            }
        }
        System.out.println("Winner is " + table.getTeams().get(indexWinner).getId() + " with " + table.getCards().get(indexWinner));
        return table.getPlayers().get((indexWinner));
    }

    public static void calculatePoints(ArrayList<Player> players) {
        for(Player p : players){
            calculatePointsForPlayer(p);
        }
    }

    public static void calculatePointsForPlayer(Player player) {
        int points = 0;
        for(Card c : player.getDeckPlayer().getCards()){
            if(cardsPoints.containsKey(c.getValue())){
                points += cardsPoints.get(c.getValue());
            }
        }
        player.setPoints(player.getPoints() + points);
        System.out.println(player.getId() + " got " + points + " points");
        System.out.println("Now he has " + player.getPoints() + " points\n");
    }

}
