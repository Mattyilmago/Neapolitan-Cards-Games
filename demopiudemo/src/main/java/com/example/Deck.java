package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Deck {
    ArrayList<Card> cards;
   private final Character[] seeds = {'B', 'C', 'D', 'S'};

    public Deck() {
        ArrayList<Card> deck = new ArrayList<Card>(40);
        for (int s = 0; s < 4; s++) {
            for (int i = 1; i <= 10; i++) {
                deck.add(new Card(i, seeds[s]));
                System.out.println(deck.get(i - 1 + 10 * s).toString());
            }
        }
        this.cards = deck;
    }

    /**
     * Shuffle the deck
     * @param deck
     */
    public static void shuffle(Deck deck) {

        Random random = new Random();

        for (int i = 0; i < deck.cards.size(); i++) {
            int randomIndexToSwap = random.nextInt(deck.cards.size());
            Card temp = deck.cards.get(randomIndexToSwap);
            deck.cards.set(randomIndexToSwap,deck.cards.get(i));
            deck.cards.set(i,temp);
        }
        System.out.println(deck);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public Character[] getSeeds() {
        return seeds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;
        return Objects.equals(cards, deck.cards);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(cards);
        result = 31 * result + Arrays.hashCode(seeds);
        return result;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "cards=" + cards +
                ", seeds=" + Arrays.toString(seeds) +
                '}';
    }

    public static void main(String[] args) {
        Deck deck = new Deck();
        shuffle(deck);
        shuffle(deck);
        return;
    }
}
