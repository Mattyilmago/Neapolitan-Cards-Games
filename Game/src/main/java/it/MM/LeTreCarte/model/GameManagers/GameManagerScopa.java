package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.TableController;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.CardContainer;
import it.MM.LeTreCarte.model.card.cardcontainer.Table;

import java.util.ArrayList;
import java.util.HashMap;


public class GameManagerScopa {
    private static CardContainer vcurr;
    private static CardContainer vbest;
    private static Integer[] scope = {0,0};

    //hashmap per i punti della primiera
    private static final HashMap<Integer, Integer> conversionTablePrimiera = new HashMap<>(){{
        put(7, 21);
        put(6, 18);
        put(1, 16);
        put(5, 15);
        put(4, 14);
        put(3, 13);
        put(2, 12);
        put(8, 10);
        put(9, 10);
        put(10, 10);
    }};


    /**
     * Backtracking algorithm that calculates the best choice of won cards in the game of Scopa
     * @param card played card
     * @param sum current values'sum of the chosen cards
     * @param lvl level of algorithm
     * @param i current level of algorithm
     * @param hasMostDenari true if the player has at least 5 denari's cards in his deckplayer
     */
    public static void calculateWonCardsRec(Card card, int sum, int lvl, int i, boolean hasMostDenari, ArrayList<Card> tableCards) {
        if (i == lvl || sum == card.getValue()) {
            if (sum == card.getValue()) {
                System.out.println("vcurr = " + vcurr + "   vbest = " + vbest);

                if (vbest.contains(new Card(7, 'D'))) { //se entrambi gli arraylist contengono il settebbello controllo se al giocatore serve il maggior numero di carte di denari
                    if (vcurr.contains(new Card(7, 'D'))) {
                        if (!hasMostDenari && vcurr.cardsNumberWithSameSeed('D') > vbest.cardsNumberWithSameSeed('D')) { //se servono i denari e vcurr ne contiene di più scambio
                            vcurr.copyIn(vbest);
                        } else if (vcurr.size() > vbest.size()) { //se non servono i denari e vcurr ha più carte di vbest scambio
                            vcurr.copyIn(vbest);
                        }
                    }
                    return;

                } else { //se solo vcurr contiene il settebbello scambio
                    if (vcurr.contains(new Card(7, 'D'))) {
                        vcurr.copyIn(vbest);
                    } else if (!hasMostDenari && vcurr.cardsNumberWithSameSeed('D') > vbest.cardsNumberWithSameSeed('D')) { //se servono i denari e vcurr ne contiene di più scambio
                        vcurr.copyIn(vbest);
                    } else if (vcurr.size() > vbest.size()) { //se non servono i denari e vcurr ha più carte di vbest scambio
                        vcurr.copyIn(vbest);
                    }
                }
            }
            return;
        }

        if (!(sum + tableCards.get(i).getValue() > card.getValue())) {
            vcurr.addCard(tableCards.get(i));
            sum += vcurr.getLast().getValue();
            calculateWonCardsRec(card, sum, lvl, i + 1, hasMostDenari, tableCards);

            sum -= vcurr.getLast().getValue();
            vcurr.removeCard(tableCards.get(i));
        }

        calculateWonCardsRec(card, sum, lvl, i + 1, hasMostDenari, tableCards);
    }

    /**
     * Calculates and return the cards won
     * @param card played card
     * @param player current player
     * @return cards won
     */
    public static ArrayList<Card> calculateWinTurn(Card card, Player player, Table table) {
        ArrayList<Card> cardsWon = new ArrayList<>();
        if (!table.getCards().isEmpty()){
            if (table.containsCardSameValue(card)) { //se esiste già una carta a terra con lo stesso valore prendo la carta
                cardsWon.add(table.bestCardSameValue(card));
                cardsWon.add(card);

            } else {

                boolean hasMostDenari = player.getDeckPlayer().cardsNumberWithSameSeed('D') > 5;

                ArrayList<Card> tableCards = new ArrayList<>(table.getCards());

                vbest = null;
                calculateWonCardsRec(card, 0, table.getCards().size(), 0, hasMostDenari, tableCards);
                if(vbest != null) {
                    cardsWon.addAll(vbest.getCards());
                    cardsWon.add(card);
                }
            }
        }
        if(table.getCards().equals(cardsWon)){ //se erano le ultime carte il giocatore ha fatto scopa
            scope[table.getPlayers().indexOf(player)] += 1;
        }

        return cardsWon;
    }

    /**
     * Calculates the points of Primiera for each player
     * @return Player that wins Primiera
     */
    public static Player playerWinsPrimiera(Table table) {
        int[] points = new int[2];

        for (int i = 0; i < 2; i++) {
            for (Character s : Card.seeds) {
                points[i] += table.getPlayers().get(i).getHighestValueForSeed(conversionTablePrimiera, s);
            }
        }

        System.out.println(table.getPlayers().getFirst().getId() + " Has: " + points[0]);
        System.out.println(table.getPlayers().getLast().getId() + " Has: " + points[1]);
        if (points[0] > points[1]) {
            return table.getPlayers().getFirst();
        } else if (points[0] < points[1]) {
            return table.getPlayers().getLast();
        }
        return null;
    }

    /**
     * Calculates points for all the players
     */
    public static void calculatePointsScopa(Table table) {
        for (Player p : table.getPlayers()) {
            calculatePointsForPlayer(p, table);
        }
    }

    /**
     * Calculates points of the single player
     * @param player current player
     */
    public static void calculatePointsForPlayer(Player player, Table table) {
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
        if (player.equals(playerWinsPrimiera(table))) {
            System.out.println(player.getId() + " wins primiera");
            points++;
        }

        points += scope[table.getPlayers().indexOf(player)];
        player.setPoints(player.getPoints() + points);
        System.out.println(player.getId() + " got " + points + " points");
        System.out.println("Now he has " + player.getPoints() + " points");
    }
}