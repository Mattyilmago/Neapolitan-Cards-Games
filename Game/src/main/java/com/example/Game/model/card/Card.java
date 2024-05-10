package com.example.Game.model.card;

import java.util.Objects;

/**
 * Class card that accept only cards with value between 1 and 10 and seeds in [BCDS]
 * usage: B -> bastoni, C -> coppe, D -> denari, S -> spade
 */
public class Card {
    int value;
    Character seed; //seme della carta, uso: B -> bastoni, C -> coppe, D -> denari, S -> spade
    public static final Character[] seeds = {'B', 'C', 'D', 'S'};

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value < 1 || value > 10)
            throw new IllegalArgumentException("ERRORE il valore della carta non è compreso tra 1 e 10");

        this.value = value;
    }

    public char getSeed() {
        return seed;
    }

    public void setSeed(Character seed) {
        if (!seed.toString().toUpperCase().matches("[BCDS]"))
            throw new IllegalArgumentException("ERRORE il seme non è corretto.");

        this.seed = seed;
    }

    public Card(int value, Character seed) {
        setSeed(seed);
        setValue(value);
        this.value = value;
        this.seed = seed;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value && Objects.equals(seed, card.seed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, seed);
    }

    @Override
    public String toString() {
        return "Card{" +
                "value=" + value +
                ", seed=" + seed +
                '}';
    }

    public static void main(String[] args) {
        Card c = new Card(1,'b');
        System.out.println(c);
    }
}
