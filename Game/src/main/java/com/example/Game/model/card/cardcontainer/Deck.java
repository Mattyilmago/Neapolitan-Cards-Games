package com.example.Game.model.card.cardcontainer;

import com.example.Game.model.card.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Deck extends CardContainer {
    private final int length = 40;
    private final Character[] seeds = {'B', 'C', 'D', 'S'};

    public Deck() {
        ArrayList<Card> deck = new ArrayList<Card>(length);
        for (int s = 0; s < 4; s++) {
            for (int i = 1; i <= 10; i++) {
                deck.add(new Card(i, seeds[s]));
                System.out.println(deck.get(i - 1 + 10 * s).toString());
            }
        }
        this.cards = deck;
    }

    public void shuffle() {
        Random random = new Random();

        for (int i = 0; i < cards.size(); i++) {
            int randomIndexToSwap = random.nextInt(cards.size());
            Card temp = cards.get(randomIndexToSwap);
            cards.set(randomIndexToSwap, cards.get(i));
            cards.set(i, temp);
        }
        System.out.println(cards);
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
                cards + '}';
    }

    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.shuffle();
        deck.add(new Card(2, 'S'));
        System.out.println(deck);
    }
}
