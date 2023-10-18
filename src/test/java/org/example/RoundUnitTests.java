package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    void dealCardsToPlayerHands(int n){
        Deck fullDeck = Deck.FullDeck();
        for (Player player: players) {
            fullDeck.dealCardsTo(player.getHand(), n);
        }
    }

    void dealCardsToPlayerInjuryDecks(int n){
        Deck fullDeck = Deck.FullDeck();
        for (Player player: players) {
            fullDeck.dealCardsTo(player.getInjuryDeck(), n);
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
}