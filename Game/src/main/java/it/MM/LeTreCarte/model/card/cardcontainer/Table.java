package it.MM.LeTreCarte.model.card.cardcontainer;

import it.MM.LeTreCarte.Player;
import it.MM.LeTreCarte.model.card.Card;

import java.util.ArrayList;

public class Table extends CardContainer {
    ArrayList<Player> teams = new ArrayList<Player>();
    ArrayList<Player> players = new ArrayList<Player>();

    public Table(ArrayList<Player> team, ArrayList<Player> players) {
        this.cards = new ArrayList<Card>();
        this.teams = team;
        this.players = players;
    }

    public void setTeams(ArrayList<Player> teams) {
        this.teams = teams;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Player> getTeams() {
        return teams;
    }

    public Player getTeam(int index) {
        if (index > 1) {
            throw new IllegalArgumentException();
        }
        return getTeams().get(index);
    }

    @Override
    public String toString() {
        return "Table{" + "teams=" + teams + ", cards=" + cards + '}';
    }
}
