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
        StringWriter output = new StringWriter();
        String input = String.join("\n", playerNames.stream().limit(numPlayers).toList());
        TournamentGame game = new TournamentGame(new Scanner(input), new PrintWriter(output), numPlayers);
        return game;
    }

    @ParameterizedTest
    @DisplayName("U-TEST 067: TournamentGame class can be created with valid number of players, can be prompted for valid player names for each player.")
    @ValueSource(ints = {-1, 0, 2, 3, 4, 5, 6, 7})
    void testGamePlayerSetupPrompts(int numPlayers){
        StringWriter output = new StringWriter();

        if (numPlayers < TournamentGame.MIN_PLAYERS || numPlayers > TournamentGame.MAX_PLAYERS){
            String badInput = String.join("\n", playerNames.stream().toList());
            assertThrows(IllegalArgumentException.class, () -> new TournamentGame(new Scanner(badInput), new PrintWriter(output), numPlayers));
            return;
        }
        String input = String.join("\n", playerNames.stream().limit(numPlayers).toList());

        TournamentGame game = assertTimeoutPreemptively(Duration.ofSeconds(1), () -> new TournamentGame(new Scanner(input), new PrintWriter(output), numPlayers));

        assertEquals(numPlayers, game.getNumPlayers());

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

    @Test
    @DisplayName("U-TEST 069: TournamentGame class can play through a round and keep track of number of rounds played.")
    void testPlayRound(){
        StringWriter output = new StringWriter();
        Player.setStartingHealth(100);
        TournamentGame game = setupGame(TournamentGame.MAX_PLAYERS);

        assertEquals(0, game.getRoundsPlayed());

        // Play round with pre-set deck where all cards can always be played. This is to make play inputs easier
        List<Card> cardList = CardGenerator.generateBasicCards(80, CardSuit.SWORDS).toList();
        Deck gameDeck = new Deck(cardList);
        String input = "1\n".repeat(TournamentGame.MAX_PLAYERS * Round.MAX_MELEES); // All players choose the first card every time for every melee in the round
        game.playRound(new Scanner(input), new PrintWriter(output), gameDeck);

        assertEquals(1, game.getRoundsPlayed());

        // If any of the players are dead, the game is over
        if (game.getPlayers().stream().anyMatch(p -> p.getHealth() == 0)){
            assertEquals(true, game.isGameOver());
            // Can't play the game when it's already over!
            assertThrows(IllegalStateException.class, () -> game.playRound(new Scanner(input), new PrintWriter(output), new Deck(cardList)));
        } else {
            assertEquals(false, game.isGameOver());
        }
    }
}