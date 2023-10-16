package org.example;

import java.util.List;

public class Melee {
    private List<Player> players;
    private Player playerLeader;
    private int leaderIndex;

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
}
