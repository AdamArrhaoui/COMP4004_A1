package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DeckUnitTests {
    @Test
    @DisplayName("U-TEST 016: New empty deck contains no cards")
    void testNewEmptyDeckIsEmpty(){
        Deck deck = new Deck();
        ArrayList<Card> deckCards = deck.getCards();
        assertNotNull(deckCards);
        assertEquals(0, deckCards.size());
    }
}