package org.example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Melee {
    static final int SHAME_DAMAGE = 5;
    private List<Player> players;
    private List<Card> playedCards;
    private Player playerLeader;
    private int leaderIndex;
    private CardSuit meleeSuit;

    Melee(List<Player> players, Player leader){
        this.leaderIndex = players.indexOf(leader);
        if (this.leaderIndex == -1) throw new IllegalArgumentException("Melee leader not in supplied player list!");
        this.players = players;
        this.playerLeader = leader;
        this.playedCards = new ArrayList<Card>();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public Player getPlayerLeader() {
        return playerLeader;
    }

    public int getLeaderIndex(){
        return leaderIndex;
    }

    public CardSuit getMeleeSuit() {
        return meleeSuit;
    }

    public void playFirstCard(Scanner input, PrintWriter output) {
        if (!playedCards.isEmpty()) throw new IllegalStateException("There have already been cards played!");
        Card chosenCard = playerLeader.promptPlayFirstCard(input, output);
        meleeSuit = chosenCard.getSuit();
        playedCards.add(chosenCard);
    }

    public void playCards(Scanner input, PrintWriter output) {
        playFirstCard(input, output);
        for (int i = 1; i < players.size(); i++) {
            Player currPlayer = players.get((leaderIndex + i) % players.size());
            Card chosenCard = currPlayer.promptPlayCard(meleeSuit, input, output);
            if (chosenCard != null) playedCards.add(chosenCard);
        }
    }
}
