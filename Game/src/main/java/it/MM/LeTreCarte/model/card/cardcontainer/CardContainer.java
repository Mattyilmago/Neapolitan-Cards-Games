package it.MM.LeTreCarte.model.card.cardcontainer;

import it.MM.LeTreCarte.model.card.Card;

import java.util.ArrayList;
import java.util.Objects;

public abstract class CardContainer {
    ArrayList<Card> cards;

    public CardContainer() {
        this.cards = new ArrayList<>();
    }

    public CardContainer(int length) {
        this.cards = new ArrayList<>(length);
    }
    public ArrayList<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public void removeLast() {
        if (cards.isEmpty()) {
            return;
        }
        cards.removeLast();
    }

    public void copyIn(CardContainer dest){
        dest.clear();
        for(Card card : cards){
            dest.addCard(card);
        }
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

    public Boolean containsCardSameValue(Card card){
        for(Card c : this.cards){
            if(c.getValue() == card.getValue()) {
                return true;
            }
        }
        return false;
    }

    public Card bestCardSameValue(Card card){
        if(contains(new Card(card.getValue(), 'D'))){
            return new Card(card.getValue(), 'D');
        }

        for(Card c : this.cards){
            if(card.getValue() == c.getValue()){
                return c;
            }
        }
        return null;
    }

    /**
     * Calculate the number of cards with same seed
     *
     * @param seed
     * @return the nuber of cards with same seed
     */
    public int cardsNumberWithSameSeed(Character seed) {
        int numberOfCards = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getSeed() == seed) {
                numberOfCards++;
            }
        }
        return numberOfCards;
    }

    /**
     * Create an arraylist of card filled with cards on same seed
     *
     * @param seed
     * @return Arraylist of cards
     */
    public ArrayList<Card> cardsWithSameSeed(Character seed) {
        ArrayList<Card> cardsSameSeed = new ArrayList<>();
        for (Card c : cards) {
            if (c.getSeed() == seed) {
                cardsSameSeed.add(c);
            }
        }
        return cardsSameSeed;
    }

    public void moveCardTo(Card card, CardContainer dest) {
        dest.addCard(card);
        this.removeCard(card);
    }

    public void moveCardsTo(CardContainer dest) {
        ArrayList<Card> cardsToMove = new ArrayList<>();
        for (Card card : this.cards) {
            dest.addCard(card);
            cardsToMove.add(card);
        }
        this.cards.removeAll(cardsToMove);
    }

    public Card getLast() {
        return this.cards.getLast();
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

    @Override
    public String toString() {return "Cards=" + cards + '}';}

}
