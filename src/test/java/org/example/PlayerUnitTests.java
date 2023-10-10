package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class PlayerUnitTests {
    @ParameterizedTest
    @DisplayName("U-TEST 036: New player has non-blank name")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "Billy", "Bobby", "Sammy"})
    void testNewPlayerNonEmptyName(String testName){
        if (testName == null || testName.isBlank()){
            assertThrows(IllegalArgumentException.class, () -> new Player(testName));
            return;
        }
        Player player = new Player(testName);
        assertEquals(testName, player.getName());
    }

    @Test
    @DisplayName("U-TEST 037: New Player has empty Deck as their hand, and empty Deck as injuryDeck")
    void testNewPlayerEmptyHandEmptyInjuryDeck(){
        Player player = new Player("Bobby");
        assertNotNull(player.getHand());
        assertTrue(player.getHand().getCards().isEmpty());

        assertNotNull(player.getInjuryDeck());
        assertTrue(player.getInjuryDeck().getCards().isEmpty());
    }

    @Test
    @DisplayName("U-TEST 038: New Deck created as player's hand has reference to the player that the deck belongs to.")
    void testNewPlayerHandsPlayerReference(){
        Deck testDeck = new Deck();
        assertNull(testDeck.getPlayerOwner());
        Player player = new Player("Bobby");
        assertSame(player, player.getHand().getPlayerOwner());
    }

    @Test
    @DisplayName("U-TEST 041: Player can be prompted to select any card from their hand using the index")
    void testPlayerCardPrompt(){
        String input = "1\n";
        StringWriter output = new StringWriter();
        // Prompting card from player with an empty deck should result in Null
        Player player = new Player("Billy");
        assertNull(player.promptAnyCard(new Scanner(input), new PrintWriter(output)));

        Deck fullDeck = Deck.FullDeck();
        fullDeck.shuffle();
        fullDeck.dealCardsTo(player.getHand(), 12);

        // Test that each card in the player's hand can be prompted for and returned correctly
        for (int i = 1; i <= 12; i++) {
            input = i + "\n";
            Card chosenCard = player.promptAnyCard(new Scanner(input), new PrintWriter(output));
            assertNotNull(chosenCard);
            assertSame(player.getHand().getCards().get(i - 1), chosenCard);
        }

        // Test that the player will get continue to get re-prompted for a card choice if they give invalid input,
        // and the card returned is valid
        Random random = new Random();
        int randCardIdx = random.nextInt(1, 13);
        String garbInput = "-1\n0\n100\nasfefsf\nNaN\n\n";
        Card resultCard = assertDoesNotThrow(() -> {
            return player.promptAnyCard(new Scanner(garbInput + randCardIdx + "\n"), new PrintWriter(output));
        });
        assertSame(player.getHand().getCards().get(randCardIdx - 1), resultCard);
    }

    @Test
    @DisplayName("U-TEST 042: Player can be prompted to select a card from their hand to discard.")
    void testPlayerCardDiscardPrompt(){
        String input = "1\n";
        StringWriter output = new StringWriter();

        Player player = new Player("Bobby");
        // Test player discards nothing when their hand is empty
        assertNull(player.promptDiscardCard(new Scanner(input), new PrintWriter(output)));

        Deck fullDeck = Deck.FullDeck();
        fullDeck.shuffle();
        fullDeck.dealCardsTo(player.getHand(), 12);
        Random random = new Random();

        for (int i = 12; i > 0; i--) {
            int randIdx = random.nextInt(1, i + 1);
            input = randIdx + "\n";
            Card discarded = player.promptDiscardCard(new Scanner(input), new PrintWriter(output));
            assertNotNull(discarded);
            assertEquals(i-1, player.getHand().getCards().size());
            assertFalse(player.getHand().getCards().contains(discarded));
        }
    }

    @Test
    @DisplayName("U-TEST 043: Player can be prompted to select a valid card suit.")
    void testPlayerCardSuitPrompt(){
        // Map of cardsuits to input strings that should result in that suit
        // Testing for no case sensitivity, multiple spellings, and also using the suit index
        Map<CardSuit,List<String>> validSuitStrings = Map.of(
                CardSuit.SWORDS, List.of("Sw", "SWoRds", "SW     ", "sword", "1"),
                CardSuit.ARROWS, List.of(" AR", "ar", "arr ows", "Arrows ", "2"),
                CardSuit.SORCERY, List.of("SO", "sO", " SOr  ", "SOrcery", "3", " 3 "),
                CardSuit.DECEPTION, List.of("DECEPT", "dec", "DE", "dece pt ION  ", " 4")
        );

        // List of invalid inputs. Inputting these should result in a re-prompt
        List<String> invalidSuitStrings = List.of("", "afsefsva", "s", " s", "a", "-1", "0", "ANY", "any", "BASIC", "NaN");

        Player player = new Player("Bobby");
        StringWriter output = new StringWriter();

        for (Map.Entry<CardSuit,List<String>> entry : validSuitStrings.entrySet()){
            CardSuit expectedSuit = entry.getKey();
            List<String> inputStrings = entry.getValue();
            for(String str : inputStrings){
                String input = str + "\n";
                // The prompt should complete almost instantly.
                // If it takes longer than 1 second, it's probably re-prompting, which means test failed.
                CardSuit resultSuit = assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
                    return player.promptCardSuit(new Scanner(input), new PrintWriter(output));
                }, String.format("promptCardSuit timed out! Input parsing probably failed for string '%s'", input));
                assertNotNull(resultSuit);
                assertEquals(expectedSuit, resultSuit,
                        String.format("Wrong card suit returned. \nInput: '%s' \nResult: %s \n Expected: %s",
                                input, resultSuit, expectedSuit));
            }
        }

        // Test invalid inputs correctly re-prompting. Using Sword value as a test
        String invalidInputChain = String.join("\n", invalidSuitStrings);
        CardSuit resultSuit = assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            return player.promptCardSuit(new Scanner(invalidInputChain + "\nSW"), new PrintWriter(output));
        });
        assertNotNull(resultSuit);
        assertEquals(CardSuit.SWORDS, resultSuit);
    }
}