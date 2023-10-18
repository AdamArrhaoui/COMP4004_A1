package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class RoundUnitTests {
    static List<Player> players;
    static Random random;

    @BeforeAll
    static void setupPlayers(){
        Player.setStartingHealth(Player.DEFAULT_STARTING_HEALTH);
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

    void dealCardsToPlayerHands(int n){
        Deck fullDeck = Deck.FullDeck();
        for (Player player: players) {
            fullDeck.dealCardsTo(player.getHand(), n);
        }
    }

    void dealCardsToPlayerInjuryDecks(int n) {
        dealCardsToPlayerInjuryDecks(n, false);
    }

    void dealCardsToPlayerInjuryDecks(int n, boolean shuffle){
        Deck fullDeck = Deck.FullDeck();
        if (shuffle) fullDeck.shuffle();
        for (Player player: players) {
            fullDeck.dealCardsTo(player.getInjuryDeck(), n);
        }
    }

    void dealCardsSoPlayerLoses(int loserIndex){
        // Choose player to lose
        Player loserPlayer = players.get(loserIndex);

        int cardVal = 2;
        for (Player player: players) {
            if (player == loserPlayer){
                // Make loser player have lowest cardVal
                cardVal = 1;
            }
            player.getHand().addCard(new Card(CardSuit.SWORDS, cardVal));
            cardVal++;
        }
    }

    void dealCardsSoNobodyLoses(){
        for (Player player : players) {
            player.getHand().addCard(new Card(CardSuit.ARROWS, 10));
        }
    }

    void setAllPlayersStartingHealth(int health){
        Player.setStartingHealth(health);
        for (Player player : players) {
            player.setHealth(health);
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

    @Test
    @DisplayName("U-TEST 063: Round can perform initial setup. All player's hands and injury decks are cleared. Players are then dealt 12 random cards from a shuffled full deck. Round setup can only occur once per round.")
    void testRoundSetup(){
        // Deal random cards to players hands and injury decks
        dealCardsToPlayerHands(5);
        dealCardsToPlayerInjuryDecks(5);

        Round round = new Round(players, 1);
        assertDoesNotThrow(round::setupRound);

        // Make new full ordered deck to test if player hands are actually shuffled
        Deck fullTestDeck = Deck.FullDeck();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            assertEquals(Round.MAX_MELEES, player.getHand().getCards().size());
            assertTrue(player.getInjuryDeck().getCards().isEmpty());
            List<Card> orderedCards = fullTestDeck.getCards().subList(i * Round.MAX_MELEES, (i+1) * Round.MAX_MELEES);
            assertFalse(orderedCards.stream().allMatch(c -> c.cardEquals(player.getHand().getCards().get(orderedCards.indexOf(c)))));
        }
        // Make sure round setup can only occur once
        assertThrows(IllegalStateException.class, round::setupRound);
    }

    @Test
    @DisplayName("U-TEST 064: Round can play a melee lead by current leader, then set the next leader according to who lost the melee.")
    void testRoundSingleMelee(){
        Round round = new Round(players, 1);

        String input = "1\n".repeat(5); // All 5 players choose the first card
        StringWriter output = new StringWriter();

        // Can't play melee if player's hands are empty
        assertThrows(IllegalStateException.class, () -> round.playNextMelee(new Scanner(input), new PrintWriter(output)));
        // Set up so non-leading player loses the melee
        int loserIndex = random.nextInt(1, players.size());
        dealCardsSoPlayerLoses(loserIndex);

        // Make sure the round leader changes to the melee loser
        Player expectedLeader = players.get(loserIndex);
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> round.playNextMelee(new Scanner(input), new PrintWriter(output)));
        assertEquals(expectedLeader, round.getCurrentLeader());

        // Set up so that no player loses (all cards are same value)
        dealCardsSoNobodyLoses();
        // Make sure the round leader doesn't change if the melee has no losers
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> round.playNextMelee(new Scanner(input), new PrintWriter(output)));
        assertEquals(expectedLeader, round.getCurrentLeader());
    }

    @Test
    @DisplayName("U-TEST 065: Round can end, dealing and displaying the number of injury points suffered by each player in this round and their remaining health.")
    void testRoundEnd(){
        setAllPlayersStartingHealth(1000);
        Round round = new Round(players, 1);
        StringWriter output = new StringWriter();

        dealCardsToPlayerInjuryDecks(3, true);
        round.endRound(new PrintWriter(output));

        // round can only be ended once
        assertThrows(IllegalStateException.class, () -> round.endRound(new PrintWriter(output)));

        output.flush();
        String roundOutput = output.toString();
        for (Player player : players) {
            int playerInjury = player.getInjuryDeck().getTotalInjury();
            int expectedHealth = Player.getStartingHealth() - playerInjury;
            assertEquals(expectedHealth, player.getHealth());
            // Make sure the player's name, injury, and remaining health is output
            assertTrue(roundOutput.contains(player.getName()));
            assertTrue(roundOutput.contains(Integer.toString(playerInjury)));
            assertTrue(roundOutput.contains(Integer.toString(player.getHealth())));
        }
    }
}