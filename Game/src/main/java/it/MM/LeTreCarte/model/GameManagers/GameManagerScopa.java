package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.CardContainer;
import it.MM.LeTreCarte.model.card.cardcontainer.Table;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerScopa extends GameManager {
    private final HashMap<Integer, Integer> conversionTablePrimiera;//hashmap per i punti della primiera
    private CardContainer vcurr;
    private CardContainer vbest;
    private int[] scopa;

    public GameManagerScopa(Table table) {
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

        this.scopa = new int[table.getPlayers().size()];
        this.table = table;
    }

    public static void main(String[] args) {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Mago"));
        players.add(new Player("Chop"));
        players.add(new Player("Baricco"));
        players.add(new Player("Lore"));

        Table table = new Table(players);
        table.addCard(new Card(10, 'C'));
        table.addCard(new Card(10, 'D'));
        table.addCard(new Card(1, 'C'));
        table.addCard(new Card(1, 'D'));
        table.addCard(new Card(2, 'D'));


        GameManagerScopa gms = new GameManagerScopa(table);
        gms.calculateWinTurn(new Card(10, 'D'), players.getFirst());
    }

    /**
     * Backtracking algorithm that calculates the best choice of won cards
     * @param card played card
     * @param sum current values'sum of the chosen cards
     * @param lvl level of algorithm
     * @param i current level of algorithm
     * @param hasMostDenari true if the player has at least 5 denari's cards
     */
    public void calculateWonCardsRec(Card card, int sum, int lvl, int i, boolean hasMostDenari) {
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

        if (!(sum + table.getCards().get(i).getValue() > card.getValue())) {
            vcurr.addCard(table.getCards().get(i));
            sum += vcurr.getLast().getValue();
            calculateWonCardsRec(card, sum, lvl, i + 1, hasMostDenari);

            sum -= vcurr.getLast().getValue();
            vcurr.removeCard(table.getCards().get(i));
        }

        calculateWonCardsRec(card, sum, lvl, i + 1, hasMostDenari);
    }

    /**
     * Calculates the cards won
     * @param card played card
     * @param player current player
     */
    public void calculateWinTurn(Card card, Player player) {
        if (table.getCards().isEmpty()) table.getCards().add(card);
        else {
            if (table.containsCardSameValue(card)) { //se esiste già una carta a terra con lo stesso valore prendo la carta
                Card wins = table.bestCardSameValue(card);
                table.moveCardTo(wins, player.getDeckPlayer());
                player.getDeckPlayer().addCard(card);
            } else {

                this.vcurr = new CardContainer(table.size()) {};
                this.vbest = new CardContainer(table.size()) {};

                boolean hasMostDenari = player.getDeckPlayer().cardsNumberWithSameSeed('D') > 5;
                calculateWonCardsRec(card, 0, table.getCards().size(), 0, hasMostDenari);

                System.out.println(vbest); //GODO FUNZIONA

                for(Card c : vbest.getCards()){
                    table.removeCard(c);
                }

            }
        }
        if(table.getCards().isEmpty()){ //se erano le ultime carte il giocatore ha fatto scopa
            scopa[table.getPlayers().indexOf(player)] += 1;
        }
    }

    /**
     * Calculates the points of Primiera for each player
     * @return Player that wins Primiera
     */
    public Player playerWinsPrimiera() {
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
    public void calculatePointsScopa() {
        for (Player p : table.getPlayers()) {
            calculatePointsForPlayer(p);
        }
    }

    /**
     * Calculates points of the single player
     * @param player current player
     */
    public void calculatePointsForPlayer(Player player) {
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
        if (player.equals(playerWinsPrimiera())) {
            System.out.println(player.getId() + " wins primiera");
            points++;
        }

        points += scopa[table.getPlayers().indexOf(player)];
        player.setPoints(player.getPoints() + points);
        System.out.println(player.getId() + " got " + points + " points");
        System.out.println("Now he has " + player.getPoints() + " points");
    }
}