package com.example;

import java.util.ArrayList;

public abstract class CardContainer {
    ArrayList<Card> cards;

    public void add(Card card){
        cards.add(card);
    }
}
