package com.example.Game.model.card.cardcontainer;

import com.example.Game.model.card.Card;

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
