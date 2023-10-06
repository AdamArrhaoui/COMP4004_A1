package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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
}