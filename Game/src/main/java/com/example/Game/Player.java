package com.example.Game;

import com.example.Game.model.GameManagers.*;
import com.example.Game.model.card.Card;
import com.example.Game.model.card.cardcontainer.Deck;
import com.example.Game.model.card.cardcontainer.DeckPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Player {
    private String Id;
    private DeckPlayer deckPlayer; //Mazzetto del giocatore
    private int points;

    public Player(String Id) {
        this.Id = Id;
        this.deckPlayer = new DeckPlayer();
        this.points = 0;
    }

    public static void main(String[] args) {
        Player Chop = new Player("Chop");
        Player Mago = new Player("Mago");

        ArrayList<Player> players = new ArrayList<>();

        players.add(Chop);
        players.add(Mago);

        Deck deck = new Deck();
        deck.shuffle();

        while (Mago.getPoints() < 21 && Chop.getPoints() < 21) {
            while (!deck.getCards().isEmpty()) {
                Random random = new Random();
                int i = random.nextInt(0, 2);
                deck.moveCardTo(deck.getCards().getFirst(), players.get(i).getDeckPlayer());
            }
            for(Player p : players){
                System.out.println(p.getId() + "'s cards are " + p.getDeckPlayer().size() + p.getDeckPlayer().getCards()   );
            }

            GameManagerTressette gms = new GameManagerTressette(players);
            gms.calculatePoints(players, new boolean[]{true, false});
        }
    }

    /**
     * Calculates the highest value for seed in DeckPlayer
     *
     * @param conversionTable
     * @param seed
     * @return Highest value for seed in DeckPlayer
     */
    public int getHighestValue(HashMap<Integer, Integer> conversionTable, Character seed) {
        ArrayList<Card> cardsWithSameSeed = this.deckPlayer.cardsWithSameSeed(seed);
        int highestValue = 0;

        for (Card c : cardsWithSameSeed) {
            if (conversionTable.get(c.getValue()) > highestValue) {
                highestValue = conversionTable.get(c.getValue());
            }
        }
        return highestValue;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public DeckPlayer getDeckPlayer() {
        return deckPlayer;
    }

    public void setDeckPlayer(DeckPlayer deckPlayer) {
        this.deckPlayer = deckPlayer;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return points == player.points && Objects.equals(Id, player.Id) && Objects.equals(deckPlayer, player.deckPlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, deckPlayer, points);
    }

    @Override
    public String toString() {
        return "Player{" + "Id='" + Id + '\'' + ", deckPlayer=" + deckPlayer + ", points=" + points + '}';
    }
}