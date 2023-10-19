package org.example;

import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class TournamentGame {
    static final int MIN_PLAYERS = 3;
    static final int MAX_PLAYERS = 5;
    private int numPlayers;
    private List<Player> players;

    TournamentGame(int numPlayers){
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setupPlayers(Scanner input, PrintWriter output) {
    }
}
