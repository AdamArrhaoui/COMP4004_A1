package org.example;

import java.util.List;

public class Melee {
    private List<Player> players;

    private Player playerLeader;
    Melee(List<Player> players, Player leader){}

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayerLeader() {
        return playerLeader;
    }
}
