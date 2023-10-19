package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TournamentGameTests {
    static List<String> playerNames = List.of(
            "Billy", "Bobby", "Freddy", "Hubert", "AAron"
    );
    static Random random = new Random();

    private static TournamentGame setupGame(int numPlayers){
        TournamentGame game = new TournamentGame(numPlayers);
        StringWriter output = new StringWriter();
        String input = String.join("\n", playerNames.stream().limit(numPlayers).toList());
        game.setupPlayers(new Scanner(input), new PrintWriter(output));
        return game;
    }

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

    @ParameterizedTest
    @DisplayName("U-TEST 068: TournamentGame class can announce the winners of the game")
    @ValueSource(ints = {0, 1, 2, 3})
    void testAnnounceGameResults(int numWinners){
        StringWriter output = new StringWriter();
        TournamentGame game = setupGame(TournamentGame.MAX_PLAYERS);
        List<Player> winners = new ArrayList<>();

        if (numWinners == 0){
            // Test announcing no winners
            for (Player player : game.getPlayers()){
                player.setHealth(0);
            }
        } else {
            // Test announcing 1 or more winners
            // Choose random player to lose (0 health), then choose numWinners players to win (full health). Every other player has half health.
            int loserIndex = random.nextInt(0, game.getNumPlayers());
            List<Integer> winnerIndices = random.ints(0, game.getNumPlayers()).distinct().filter(n -> n!=loserIndex).limit(numWinners).boxed().toList();
            for (int i = 0; i < game.getNumPlayers(); i++){
                Player player = game.getPlayers().get(i);
                if (i == loserIndex){
                    player.setHealth(0);
                } else if (winnerIndices.contains(i)){
                    player.setHealth(Player.getStartingHealth());
                    winners.add(player);
                } else {
                    player.setHealth(Player.getStartingHealth()/2);
                }
            }
        }

        game.announceResults(new PrintWriter(output));
        output.flush();
        String finalOut = output.toString();
        assertFalse(finalOut.isBlank());
        for (Player player : game.getPlayers()){
            assertEquals(winners.contains(player), finalOut.contains(player.getName()));
        }
    }
}