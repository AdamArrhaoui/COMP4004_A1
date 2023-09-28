package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CardGeneratorUnitTests {
    @ParameterizedTest
    @DisplayName("U-Test 024: CardGenerator creates stream of n cards with given suit, type, and value")
    @ValueSource(ints = {-1, 0, 1, 2, 5})
    void testSpecificCardStream(int numCards){
        CardType testType = CardType.BASIC;
        CardSuit testSuit = CardSuit.SWORDS;
        int testVal = 1;

        // Check error handling if numCards is below 0
        if (numCards < 0){
            assertThrows(IllegalArgumentException.class, () -> CardGenerator.generateCardStream(numCards, testType, testSuit, testVal));
            return;
        }
        // Generate card stream and convert to list for testing
        List<Card> cardList = CardGenerator.generateCardStream(numCards, testType, testSuit, testVal).toList();
        assertEquals(numCards, cardList.size());
        for (Card card : cardList) {
            // Check that each card has the same values
            assertNotNull(card);
            assertEquals(card.getType(), testType);
            assertEquals(card.getSuit(), testSuit);
            assertEquals(card.getValue(), testVal);
        }
        // Test that each card is distinct in memory
        Set<Card> cardSet = new HashSet<>(cardList);
        assertEquals(numCards, cardSet.size());
    }
}