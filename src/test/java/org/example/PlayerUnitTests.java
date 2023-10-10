package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
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
}