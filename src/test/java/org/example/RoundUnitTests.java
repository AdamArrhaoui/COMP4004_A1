package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RoundUnitTests {
    static List<Player> players;
    static Random random;

    @BeforeAll
    static void setupPlayers(){
        players = List.of(
                new Player("Billy"),
                new Player("Bobby"),
                new Player("Sammy"),
                new Player("Freddy"),
                new Player("Bobbington")
        );
        random = new Random();
    }

    @BeforeEach
    void clearCards(){
        for (Player player: players) {
            player.getHand().removeAllCards();
            player.getInjuryDeck().removeAllCards();
        }
    }

    @ParameterizedTest
    @DisplayName("U-TEST 062: Round class created from ordered list of Players, and round number. Stores initial melee leader based on the round number.")
    @ValueSource(ints = {-1, 0, 1, 2, 3, 4})
    void roundCreationTest(int roundNum){
        // Round number is 1-indexed positive int
        if (roundNum <= 0){
            assertThrows(IllegalArgumentException.class, () -> new Round(players, roundNum));
            return;
        }
        Round round = new Round(players, roundNum);
        // Make sure initial lead player corresponds to round number (round 1 first player, round 2 second player, etc.)
        assertSame(players.get((roundNum-1) % players.size()), round.getCurrentLeader());
    }
}