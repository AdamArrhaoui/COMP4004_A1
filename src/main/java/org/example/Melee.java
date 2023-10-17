package org.example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

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
            else {
                shamePlayer(currPlayer, input, output);
            }
        }
    }

    public void shamePlayer(Player player, Scanner input, PrintWriter output){
        output.println("\n%s doesn't have any cards to play! Shame on you!".formatted(player.getName()));
        output.println("Choose a card to discard:");
        output.flush();
        player.promptDiscardCard(input, output);
        player.takeDamage(SHAME_DAMAGE);
        output.println("You have taken %d damage! Remaining health: %d".formatted(SHAME_DAMAGE, player.getHealth()));
    }

    public List<Card> feintStep() {
        if (playedCards.isEmpty()) throw new IllegalStateException("Cannot feint before anyone played any cards!");
        Map<Integer, Boolean> duplicateValues = new HashMap<>();
        for (Card card : playedCards) {
            int cardVal = card.getValue();
            if (duplicateValues.putIfAbsent(cardVal, false) != null) {
                duplicateValues.put(cardVal, true);
            }
        }
        return playedCards.stream().filter(card -> !duplicateValues.getOrDefault(card.getValue(), false)).toList();
    }

    public Player determineLoser() {
        if (playedCards.isEmpty()) throw new IllegalStateException("Cannot determine loser before anyone played any cards!");
        List<Card> nonFeintCards = feintStep();
        Optional<Card> minCard = nonFeintCards.stream().min(Comparator.comparingInt(Card::getValue));
        if (minCard.isEmpty()) return null; // There is no loser! All the cards got feinted!
        Player losingPlayer = minCard.get().getDeck().getPlayerOwner();
        if (losingPlayer == null) throw new IllegalStateException("Losing card doesn't have an owner! How tf...");
        return losingPlayer;
    }
}
