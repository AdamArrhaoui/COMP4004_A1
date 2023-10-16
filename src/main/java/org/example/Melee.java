package org.example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

public class Melee {
    private List<Player> players;
    private Player playerLeader;
    private int leaderIndex;
    private CardSuit meleeSuit;

    Melee(List<Player> players, Player leader){
        this.leaderIndex = players.indexOf(leader);
        if (this.leaderIndex == -1) throw new IllegalArgumentException("Melee leader not in supplied player list!");
        this.players = players;
        this.playerLeader = leader;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayerLeader() {
        return playerLeader;
    }

    public CardSuit getMeleeSuit() {
        return meleeSuit;
    }

    public void playFirstCard(Scanner input, PrintWriter output) {
    }
}
