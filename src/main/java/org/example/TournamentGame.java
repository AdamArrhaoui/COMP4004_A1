package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TournamentGame {
    static final int MIN_PLAYERS = 3;
    static final int MAX_PLAYERS = 5;
    private int numPlayers;
    private List<Player> players;

    TournamentGame(int numPlayers){
        if (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) throw new IllegalArgumentException("Player count is invalid!");
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setupPlayers(Scanner input, PrintWriter output) {
        players = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            String nameStr = "";
            output.println();
            while (nameStr == null || nameStr.isBlank()){
                output.print("\nPlease enter a name for player %d: ".formatted(i+1));
                output.flush();
                try {
                    nameStr = input.nextLine().strip();
                } catch (Exception e){
                    output.println("\nInput invalid! Please try again\n");
                }
            }
            players.add(new Player(nameStr));
        }
    }
}
