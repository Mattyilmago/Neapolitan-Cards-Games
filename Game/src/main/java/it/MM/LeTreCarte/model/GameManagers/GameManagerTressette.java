package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Table;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerTressette extends GameManager {
    private final HashMap<Integer, Integer> cardsPoints; //hashmap per i punti di tressette
    private final HashMap<Integer, Integer> cardsRank; //hashmap per i gradi delle carte di Briscola

    public GameManagerTressette(Table table) {
        cardsPoints = new HashMap<>();
        cardsPoints.put(1, 3);
        cardsPoints.put(2, 1);
        cardsPoints.put(3, 1);
        cardsPoints.put(8, 1);
        cardsPoints.put(9, 1);
        cardsPoints.put(10, 1);

        cardsRank = new HashMap<>();
        cardsRank.put(3, 10);
        cardsRank.put(2, 9);
        cardsRank.put(1, 8);
        cardsRank.put(10, 7);
        cardsRank.put(9, 6);
        cardsRank.put(8, 5);
        cardsRank.put(7, 4);
        cardsRank.put(6, 3);
        cardsRank.put(5, 2);
        cardsRank.put(4, 1);
        this.table = table;
    }

    /**
     * Calculate who wins this turn and move table's cards to winner's deckplayer
     */
    public void calculateWinnerTurn(){
        if(table.getCards().size() != 4){
            throw new IllegalArgumentException();
        }

        int indexWinner = 0;
        Character seed = table.getCards().getFirst().getSeed();

        for(Card card : table.getCards()){
            if(card.getSeed() == seed && cardsRank.get(card.getValue()) > cardsRank.get(table.getCards().get(indexWinner).getValue())){
                indexWinner = table.getCards().indexOf(card);
            }
        }

        System.out.println("Winner is " + table.getPlayers().get(indexWinner).getId() + " with " + table.getCards().get(indexWinner));
        table.moveCardsTo(table.getPlayers().get(indexWinner).getDeckPlayer());
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

    public static void main(String[] args) {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Mago"));
        players.add(new Player("Chop"));
        players.add(new Player("Baricco"));
        players.add(new Player("Pepe"));

        Table table = new Table(players);
        GameManagerTressette gameManagerTressette = new GameManagerTressette(table);

        table.addCard(new Card(5,'d'));
        table.addCard(new Card(10,'D'));
        table.addCard(new Card(3,'b'));
        table.addCard(new Card(5,'d'));
        gameManagerTressette.calculateWinnerTurn();
    }
}
