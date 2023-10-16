package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MeleeUnitTests {

    static List<Player> players;
    static Random random;

    @BeforeAll
    static void setupPlayers(){
        players = List.of(
                new Player("Billy"),
                new Player("Bobby"),
                new Player("Sammy")
        );
        random = new Random();
    }

    @BeforeEach
    void setupCards(){
        for (Player player: players) {
            player.getHand().removeAllCards();
            player.getInjuryDeck().removeAllCards();
        }
    }

    private static Stream<Integer> providePlayerIndices(){
        return IntStream.range(0, players.size()).boxed();
    }

    @Test
    @DisplayName("U-TEST 053: Melee class created from ordered list of Players, and player as melee leader. Melee leader must be in player list.")
    void testMeleeCreation(){
        Player sussyPlayer = new Player("imposter");
        // Can't make player not in player list the melee leader
        assertThrows(IllegalArgumentException.class, () -> new Melee(players, sussyPlayer));

        Player expectedLeader = players.get(0);
        Melee melee = new Melee(players, expectedLeader);
        assertArrayEquals(players.toArray(), melee.getPlayers().toArray());
        assertSame(expectedLeader, melee.getPlayerLeader());
    }

    @ParameterizedTest
    @DisplayName("U-TEST 054: Melee class can prompt the leader to play the first card which will determine the melee's suit.")
    @EnumSource(value = CardSuit.class)
    void testLeaderPlayFirstCard(CardSuit expectedSuit){
        for (Player player: players) {
            player.getHand().addCard(new Card(CardType.ALCHEMY, expectedSuit, 1));
        }
        Melee melee = new Melee(players, players.get(0));

        String input = "1\n";
        StringWriter output = new StringWriter();

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> melee.playFirstCard(new Scanner(input), new PrintWriter(output)));
        assertEquals(expectedSuit, melee.getMeleeSuit());
    }

    @ParameterizedTest
    @DisplayName("U-TEST 055: Melee class can prompt all players to play cards, in order starting with the leader, and add the played cards to playedCards list.")
    @MethodSource("providePlayerIndices")
    void testPlayMeleeCards(int leadPlayerIndex){
        for (Player player: players) {
            player.getHand().addCard(new Card(CardSuit.SWORDS, players.indexOf(player) + 1));
        }
        Melee melee = new Melee(players, players.get(leadPlayerIndex));

        String input = "1\n".repeat(players.size());
        StringWriter output = new StringWriter();

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> melee.playCards(new Scanner(input), new PrintWriter(output)));
        assertEquals(players.size(), melee.getPlayedCards().size());

        // Make sure that the cards are played in order starting from the leader.
        for (int i = 0; i < melee.getPlayedCards().size(); i++){
            int currPlayerIndex = (melee.getLeaderIndex() + i) % players.size();
            Player currPlayer = players.get(currPlayerIndex);
            Card expectedCard = currPlayer.getHand().getCards().get(0);
            Card currCard = melee.getPlayedCards().get(i);
            assertSame(expectedCard, currCard);
        }
    }

    @Test
    @DisplayName("U-TEST 056: If a player cannot play a valid card during a melee, they are asked to discard a card and suffer immediate shame damage.")
    void testPlayerShame(){
        // Choose a random player to be shameful (other than first player who is the leader)
        Player shamedPlayer = players.get(random.nextInt(1, players.size()));
        for (Player player: players) {
            CardSuit cardSuit = CardSuit.ARROWS;
            if (player == shamedPlayer){
                // Make shamed player have incompatible suit
                cardSuit = CardSuit.SWORDS;
            }
            player.getHand().addCard(new Card(cardSuit, players.indexOf(player) + 1));
        }

        Melee melee = new Melee(players, players.get(0));
        String input = "1\n".repeat(players.size() + 1); // add 1 extra input for the shamed player discarding their card
        StringWriter output = new StringWriter();

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> melee.playCards(new Scanner(input), new PrintWriter(output)));
        // playedCards size should be 1 less because shamed player doesn't play a card
        assertEquals(players.size() - 1, melee.getPlayedCards().size());

        // Make sure shamed player's hand is empty, and their health went down by SHAME_DAMAGE amount
        assertTrue(shamedPlayer.getHand().getCards().isEmpty());
        assertEquals(Player.getStartingHealth() - Melee.SHAME_DAMAGE, shamedPlayer.getHealth());
    }
}