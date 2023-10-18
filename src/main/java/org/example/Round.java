package org.example;

import java.util.List;

public class Round {
    private List<Player> players;
    private Player currentLeader;
    private int roundNum;

    Round(List<Player> players, int roundNum){

    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentLeader() {
        return currentLeader;
    }

    public int getRoundNum() {
        return roundNum;
    }
}
