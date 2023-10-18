package org.example;

import java.util.List;

public class Round {
    static final int MAX_MELEES = 12; // number of melees played per round, also amount of cards that will be dealt.
    private List<Player> players;
    private Player currentLeader;
    private int roundNum;

    Round(List<Player> players, int roundNum){
        if (roundNum <= 0) throw new IllegalArgumentException("Round num cannot be less than or equal to 0!");
        this.players = players;
        this.roundNum = roundNum;
        currentLeader = players.get((roundNum-1)%players.size());
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

    public void setupRound() {
    }
}
