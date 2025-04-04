package org.tournamentGame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

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

    @ParameterizedTest
    @DisplayName("U-TEST 025: CardGenerator creates stream of n basic cards of specified suit ordered by values 1-15 repeating")
    @EnumSource(
            value = CardSuit.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"ANY"}
    )
    void testBasicCardStreamSpecificSuit(CardSuit suit) {
        List<Card> cardList = CardGenerator.generateBasicCards(32, suit).toList();
        for (int i = 0; i < 32 ; i++) {
            int expectedValue = i % Card.MAX_VALUE + 1;
            Card currentCard = cardList.get(i);
            assertEquals(CardType.BASIC, currentCard.getType());
            assertEquals(suit, currentCard.getSuit());
            assertEquals(expectedValue, currentCard.getValue());
        }
    }

    @Test
    @DisplayName("U-TEST 026: CardGenerator creates stream of all 60 basic weapon cards in order")
    void testBasicWeaponCardStream(){
        List<Card> cardList = CardGenerator.generateBasicCards(60, CardSuit.ANY).toList();
        CardSuit[] suits = CardSuit.values();
        for (int s = 1; s <= 4; s++) {
            CardSuit currentSuit = suits[s];
            for (int i = 0; i < 15 ; i++) {
                int expectedValue = i + 1;
                int cardIndex = (s-1) * 15 + i;
                Card currentCard = cardList.get(cardIndex);
                assertEquals(CardType.BASIC, currentCard.getType());
                assertEquals(currentSuit, currentCard.getSuit());
                assertEquals(expectedValue, currentCard.getValue());
            }
        }
    }

    @Test
    @DisplayName("U-TEST 027: CardGenerator creates stream of all 15 alchemy cards in order")
    void testAlchemyCardStream(){
        List<Card> cardList = CardGenerator.generateAlchemyCards(15).toList();
        for (int i = 0; i < 15 ; i++) {
            int expectedValue = i + 1;
            Card currentCard = cardList.get(i);
            assertEquals(CardType.ALCHEMY, currentCard.getType());
            assertEquals(CardSuit.ANY, currentCard.getSuit());
            assertEquals(expectedValue, currentCard.getValue());
        }
    }

    @Test
    @DisplayName("U-TEST 028: CardGenerator creates stream of all 80 game cards in order of 60 basic, 15 alchemy, 3 merlin, 2 apprentice")
    void testAllGameCardStream(){
        List<Card> cardList = CardGenerator.generateAllGameCards().toList();
        assertEquals(80, cardList.size());
        // Test basic cards
        CardSuit[] suits = CardSuit.values();
        CardType expectedType;
        CardSuit expectedSuit;
        int expectedValue;

        for (int i = 0; i < 80; i++){
            if (i < 60){
                // Basic Card
                expectedType = CardType.BASIC;
                expectedSuit = suits[(i / 15) + 1];
                expectedValue = (i % 15) + 1;
            } else if (i < 75) {
                // Alchemy Card
                expectedType = CardType.ALCHEMY;
                expectedSuit = CardSuit.ANY;
                expectedValue = (i % 15) + 1;
            } else if (i < 78) {
                // Merlin Card
                expectedType = CardType.MERLIN;
                expectedSuit = CardSuit.ANY;
                expectedValue = 0;
            } else {
                // Apprentice Card
                expectedType = CardType.APPRENTICE;
                expectedSuit = CardSuit.ANY;
                expectedValue = 0;
            }
            int cardIndex = i;
            Supplier<String> errorMessage = () -> "Assertion failed for card number " + cardIndex;
            Card currentCard = cardList.get(i);
            assertEquals(expectedType, currentCard.getType(), errorMessage);
            assertEquals(expectedSuit, currentCard.getSuit(), errorMessage);
            assertEquals(expectedValue, currentCard.getValue(), errorMessage);
        }
    }

}