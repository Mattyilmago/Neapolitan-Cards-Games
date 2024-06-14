package it.MM.LeTreCarte.model.card.cardcontainer;

import it.MM.LeTreCarte.model.card.Card;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Deck extends CardContainer {
    private final int length = 40;

    public Deck() {
        ArrayList<Card> deck = new ArrayList<Card>(length);
        for (int s = 0; s < 4; s++) {
            for (int i = 1; i <= 10; i++) {
                deck.add(new Card(i, Card.seeds[s]));
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
    }

    public int size() {return this.cards.size();}

    public ArrayList<Card> getCards() {return cards;}

    /**
     * Get the first card of the deck and remove from it
     * @return Fisrt card
     */
    public Card getFirst(){
        Card tmp = this.cards.getFirst();
        this.cards.removeFirst();
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        return this.cards.equals(((Deck) o).cards);
    }

    @Override
    public int hashCode() {return Objects.hash(super.hashCode(), this.cards);}

    @Override
    public String toString() {
        return "Deck{" + this.size() + " " + cards + '}';
    }

}
