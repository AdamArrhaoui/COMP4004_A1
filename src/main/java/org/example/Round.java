package org.example;

import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class Round {
    static final int MAX_MELEES = 12; // number of melees played per round, also amount of cards that will be dealt.
    private List<Player> players;
    private Player currentLeader;
    private int roundNum;
    private Deck gameDeck;
    private boolean roundIsOver = false;


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
        if (gameDeck != null) throw new IllegalStateException("Round has already been setup!");
        gameDeck = Deck.FullDeck();
        gameDeck.shuffle();
        for (Player player: players) {
            player.getHand().removeAllCards();
            player.getInjuryDeck().removeAllCards();
            gameDeck.dealCardsTo(player.getHand(), MAX_MELEES);
        }
    }

    public void playNextMelee(Scanner input, PrintWriter output) {
        if (players.stream().anyMatch(p -> p.getHand().getCards().isEmpty())) throw new IllegalStateException("Can't play melee when players have empty hands!");
        Melee melee = new Melee(players, currentLeader);
        Player loser = melee.playFullMelee(input, output);
        if (loser != null){
            currentLeader = loser;
        }
    }

    public void endRound(PrintWriter output) {
        if (roundIsOver) throw new IllegalStateException("Round is already over!");
        for (Player player : players) {
            int injury = player.getInjuryDeck().getTotalInjury();
            output.print("\n%s took %d damage from %d cards in their injury deck! ".formatted(
                    player.getName(), injury, player.getInjuryDeck().getCards().size()
            ));
            player.takeDamage(injury);
            output.print("Remaining health: %d\n\n".formatted(player.getHealth()));
        }
        roundIsOver = true;
    }
}
