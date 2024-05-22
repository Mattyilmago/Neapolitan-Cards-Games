package it.MM.LeTreCarte;

import it.MM.LeTreCarte.model.GameManagers.*;
import it.MM.LeTreCarte.model.card.Card;
import it.MM.LeTreCarte.model.card.cardcontainer.Deck;
import it.MM.LeTreCarte.model.card.cardcontainer.DeckPlayer;

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

    /**
     * Calculates the highest value for seed in DeckPlayer
     *
     * @param conversionTable
     * @param seed
     * @return Highest value for seed in DeckPlayer
     */
    public int getHighestValueForSeed(HashMap<Integer, Integer> conversionTable, Character seed) {
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