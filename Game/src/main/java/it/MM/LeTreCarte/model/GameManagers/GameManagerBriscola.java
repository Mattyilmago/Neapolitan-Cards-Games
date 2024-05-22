package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Table;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerBriscola extends GameManager{
    private final HashMap<Integer, Integer> cardsPoints; //hashmap per i punti di Briscola
    private final HashMap<Integer, Integer> cardsRank; //hashmap per i gradi delle carte di Briscola
    Character briscola;

    public GameManagerBriscola(Table table) {
        cardsPoints = new HashMap<>();
        cardsPoints.put(1, 11);
        cardsPoints.put(3, 10);
        cardsPoints.put(10, 4);
        cardsPoints.put(9, 3);
        cardsPoints.put(8, 2);

        cardsRank = new HashMap<>();
        cardsRank.put(1, 10);
        cardsRank.put(3, 9);
        cardsRank.put(10, 8);
        cardsRank.put(9, 7);
        cardsRank.put(8, 6);
        cardsRank.put(7, 5);
        cardsRank.put(6, 4);
        cardsRank.put(5, 3);
        cardsRank.put(4, 2);
        cardsRank.put(2, 1);

        this.table = table;
    }

    public Character getBriscola() {
        return briscola;
    }

    public void setBriscola(Character briscola) {
        this.briscola = briscola;
    }

    /**
     * Calculate who wins this turn and move table's cards to winner's deckplayer
     */
    public void calculateWinnerTurn() {
        if (table.getCards().size() != 4) {
            throw new IllegalArgumentException();
        }

        boolean isWinnerBriscola = false;
        int indexWinner = 0;
        Character seed = table.getCards().getFirst().getSeed();

        for(Card card : table.getCards()) {
            if(isWinnerBriscola) { //Se la carta che vince è una briscola se trovo un'altra briscola di punteggio maggiore aggiorno il vincitore
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
        System.out.println("Winner is " + table.getPlayers().get(indexWinner).getId() + " with " + table.getCards().get(indexWinner));
        table.moveCardsTo(table.getPlayers().get(indexWinner).getDeckPlayer());
    }

    @Override
    public void calculatePoints(ArrayList<Player> players) {
        for(Player p : players){
            calculatePointsForPlayer(p);
        }
    }

    @Override
    public void calculatePointsForPlayer(Player player) {
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

    public static void main(String[] args) {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Mago"));
        players.add(new Player("Chop"));
        players.add(new Player("Baricco"));
        players.add(new Player("Lore"));

        Table table = new Table(players);

        table.addCard(new Card(2,'D'));
        table.addCard(new Card(3,'D'));
        table.addCard(new Card(3,'C'));
        table.addCard(new Card(1,'C'));


        GameManagerBriscola gmb = new GameManagerBriscola(table);
        gmb.setBriscola('D');
        gmb.calculateWinnerTurn();
    }
}
