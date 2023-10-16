package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MeleeUnitTests {

    static List<Player> players;
    @BeforeAll
    static void setupPlayers(){
        players = List.of(
                new Player("Billy"),
                new Player("Bobby"),
                new Player("Sammy")
        );
    }

    @BeforeEach
    void setupCards(){
        for (Player player: players) {
            player.getHand().removeAllCards();
            player.getInjuryDeck().removeAllCards();
        }
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
}