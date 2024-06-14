package it.MM.LeTreCarte.model.card.cardcontainer;

import it.MM.LeTreCarte.model.card.Card;

import java.util.ArrayList;

public class DeckPlayer extends CardContainer {

    public DeckPlayer() {
        this.cards=new ArrayList<Card>();
    }
    public void addAll(ArrayList<Card> cards){
        cards.addAll(cards);
    }

    @Override
    public String toString() {
        return "DeckPlayer{" + "cards=" + cards + '}';
    }
}
