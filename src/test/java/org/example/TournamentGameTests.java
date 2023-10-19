package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class TournamentGameTests {
    static List<String> playerNames = List.of(
            "Billy", "Bobby", "Freddy", "Hubert", "AAron"
    );

    @ParameterizedTest
    @DisplayName("U-TEST 067: TournamentGame class can be created with valid number of players, can be prompted for valid player names for each player.")
    @ValueSource(ints = {-1, 0, 2, 3, 4, 5, 6, 7})
    void testGamePlayerSetupPrompts(int numPlayers){
        if (numPlayers < TournamentGame.MIN_PLAYERS || numPlayers > TournamentGame.MAX_PLAYERS){
            assertThrows(IllegalArgumentException.class, () -> new TournamentGame(numPlayers));
            return;
        }
        TournamentGame game = new TournamentGame(numPlayers);
        assertEquals(numPlayers, game.getNumPlayers());

        StringWriter output = new StringWriter();
        String input = String.join("\n", playerNames.stream().limit(numPlayers).toList());

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> game.setupPlayers(new Scanner(input), new PrintWriter(output)));

        assertNotNull(game.getPlayers());
        assertEquals(numPlayers, game.getPlayers().size());
        for (int i = 0; i < numPlayers; i++) {
            assertEquals(playerNames.get(i), game.getPlayers().get(i).getName());
        }

    }
}