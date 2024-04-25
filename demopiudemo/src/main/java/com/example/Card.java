package com.example;

import java.util.Objects;

/**
 *
 */
public class Card {
    int value;
    Character seed;
   final private Character[] seeds = {'B','C','D','S'};
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
            throw new IllegalArgumentException("ERRORE il seme non è corretto. Uso: B -> bastoni, C -> coppe, D -> denari, S -> spade");

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
}
