package it.MM.LeTreCarte.model.card.cardcontainer;

import it.MM.LeTreCarte.model.card.Card;

import java.util.ArrayList;

public class Hand extends CardContainer {
    public Hand() {
        this.cards = new ArrayList<Card>();
    }

    @Override
    public String toString() {
        return "Hand{" + "cards=" + cards + '}';
    }
}
