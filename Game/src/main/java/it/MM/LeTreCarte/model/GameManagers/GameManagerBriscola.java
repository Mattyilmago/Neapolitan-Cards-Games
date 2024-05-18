package it.MM.LeTreCarte.model.GameManagers;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.Card;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerBriscola extends GameManager{
    private final HashMap<Integer, Integer> conversionTableBriscola; //hashmap per i punti di Briscola

    public GameManagerBriscola(ArrayList<Player> players) {
        conversionTableBriscola = new HashMap<>();
        conversionTableBriscola.put(1, 11);
        conversionTableBriscola.put(3, 10);
        conversionTableBriscola.put(10, 4);
        conversionTableBriscola.put(9, 3);
        conversionTableBriscola.put(8, 2);
        this.players = players;
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
            if(conversionTableBriscola.containsKey(c.getValue())){
                points += conversionTableBriscola.get(c.getValue());
            }
        }
        player.setPoints(player.getPoints() + points);
        System.out.println(player.getId() + " got " + points + " points");
        System.out.println("Now he has " + player.getPoints() + " points\n");
    }
}
