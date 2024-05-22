package it.MM.LeTreCarte.model.card.cardcontainer;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.Card;

import java.util.ArrayList;

public class Table extends CardContainer{
    ArrayList<Player> players = new ArrayList<Player>();

    public Table(ArrayList<Player> players) {
        this.cards = new ArrayList<Card>();
        this.players = players;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        return "Table{" + "players=" + players + ", cards=" + cards + '}';
    }
}
