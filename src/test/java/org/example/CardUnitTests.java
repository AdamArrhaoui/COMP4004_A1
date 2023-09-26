package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardUnitTests {
    @Test
    @DisplayName("U-TEST 001: New card has non-null suit and type.")
    void testNonNullSuitAndType(){
        assertThrows(IllegalArgumentException.class, () -> new Card(null, null, 0));

        CardType testType = CardType.BASIC;
        CardSuit testSuit = CardSuit.ARROWS;
        int testVal = 0;
        Card card = new Card(testType, testSuit, testVal);
        assertEquals(card.getType(), testType);
        assertEquals(card.getSuit(), testSuit);
        assertEquals(card.getValue(), testVal);
    }
}