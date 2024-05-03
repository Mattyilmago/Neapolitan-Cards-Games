package com.example.Game.model.card.cardcontainer;

import com.example.Game.model.card.Card;

import java.util.ArrayList;
import java.util.Objects;

public abstract class CardContainer{
    ArrayList<Card> cards;

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void add(Card card) {
        cards.add(card);
    }

    public void remove(Card card) {
        cards.remove(card);
    }

    public void removeLast() {
        if (cards.isEmpty()) {
            return;
        }
        cards.remove(cards.size() - 1);
    }

    public void clear() {
        cards.clear();
    }

    public int size() {
        return cards.size();
    }

    public Boolean contains(Card card) {
        return cards.contains(card);
    }

    public int cardsWithSameSeed(Character seed) {
        int numberOfCards = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getSeed() == seed) {
                numberOfCards++;
            }
        }
        return numberOfCards;
    }

    public void moveCardTo(Card card, CardContainer dest) {
        this.remove(card);
        dest.add(card);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardContainer that = (CardContainer) o;
        return Objects.equals(cards, that.cards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cards);
    }
}
