package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class CardUnitTests {
    @Test
    @DisplayName("U-TEST 001: New card has non-null suit and type.")
    void testNonNullSuitAndType(){
        assertThrows(IllegalArgumentException.class, () -> new Card(null, null, 0));

        CardType testType = CardType.BASIC;
        CardSuit testSuit = CardSuit.ARROWS;
        int testVal = 1;
        Card card = new Card(testType, testSuit, testVal);
        assertEquals(card.getType(), testType);
        assertEquals(card.getSuit(), testSuit);
        assertEquals(card.getValue(), testVal);
    }
    @Test
    @DisplayName("U-TEST 002: Card value cannot be set to less than 0 or greater than 15.")
    void testCardValueRangeLimit(){
        CardType testType = CardType.BASIC;
        CardSuit testSuit = CardSuit.ARROWS;
        assertThrows(IllegalArgumentException.class, () -> new Card(testType, testSuit, -1));
        assertThrows(IllegalArgumentException.class, () -> new Card(testType, testSuit, 16));

        Card card = new Card(testType, testSuit, 1);
        assertThrows(IllegalArgumentException.class, () -> card.setValue(-1));
        assertThrows(IllegalArgumentException.class, () -> card.setValue(16));
    }

    @Test
    @DisplayName("U-TEST 003: New basic cards have non-zero value, non-ANY suit.")
    void testBasicCardValueSuitLimits(){
        assertThrows(IllegalArgumentException.class, () -> new Card(CardSuit.ANY, 1));
        assertThrows(IllegalArgumentException.class, () -> new Card(CardSuit.ARROWS, 0));

        Card card = new Card(CardSuit.ARROWS, 1);
        assertThrows(IllegalArgumentException.class, () -> card.setValue(0));
        assertThrows(IllegalArgumentException.class, () -> card.setSuit(CardSuit.ANY));
    }

    @Test
    @DisplayName("U-TEST 004: New alchemy cards have non-zero value.")
    void testAlchemyCardValueLimits(){
        assertThrows(IllegalArgumentException.class, () -> new Card(CardType.ALCHEMY, CardSuit.ANY, 0));
        Card card = new Card(CardType.ALCHEMY, CardSuit.ANY, 1);
        assertThrows(IllegalArgumentException.class, () -> card.setValue(0));
    }

    @ParameterizedTest
    @DisplayName("U-TEST 005: Non-Basic Cards can have suit changed.")
    @EnumSource(
            value = CardType.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"BASIC"}
    )
    void testChangeNonBasicCardSuit(CardType testType){
        Card card = new Card(testType, CardSuit.SWORDS, 1);
        for (CardSuit newSuit : CardSuit.values()){
            card.changeSuit(newSuit);
            assertEquals(newSuit, card.getSuit());
        }
    }

    @ParameterizedTest
    @DisplayName("U-TEST 006: Merlin or Apprentice cards can have value changed.")
    @EnumSource(
            value = CardType.class,
            names = {"MERLIN", "APPRENTICE"}
    )
    void testChangeMerlinApprenticeValue(CardType testType){
        Card card = new Card(testType, CardSuit.ANY, 0);
        for (int i = Card.MIN_VALUE; i <= Card.MAX_VALUE; ++i){
            card.changeValue(i);
            assertEquals(i, card.getValue());
        }
    }

    @Test
    @DisplayName("U-TEST 007: Basic card cannot have it's suit or value changed")
    void testLimitBasicCardChanges(){
        Card card = new Card(CardSuit.SWORDS, 1);
        assertThrows(IllegalStateException.class, () -> card.changeSuit(CardSuit.SORCERY));
        assertThrows(IllegalStateException.class, () -> card.changeValue(2));
    }

    @Test
    @DisplayName("U-TEST 008: Alchemy card cannot have it's value changed")
    void testLimitAlchemyCardValueChange(){
        Card card = new Card(CardType.ALCHEMY, CardSuit.ANY, 1);
        assertThrows(IllegalStateException.class, () -> card.changeValue(2));
    }

    @ParameterizedTest
    @DisplayName("U-TEST 009: Basic cards correctly poisoned")
    @EnumSource(
            value = CardSuit.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"ANY"}
    )
    void testBasicCardPoison(CardSuit testSuit){
        for (int i = Card.MIN_VALUE; i <= Card.MAX_VALUE; ++i){
            Card card = new Card(testSuit, i);
            assertEquals(Card.POISON_VALUES.get(testSuit).contains(i), card.isPoisoned());
        }
    }

    @ParameterizedTest
    @DisplayName("U-TEST 010: Non-basic cards never poisoned")
    @EnumSource(
            value = CardType.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"BASIC"}
    )
    void testNonBasicCardPoison(CardType testType){
        for (CardSuit testSuit : CardSuit.values()){
            for (int i = Card.MIN_VALUE; i <= Card.MAX_VALUE; ++i){
                Card card = new Card(testType, testSuit, i);
                assertFalse(card.isPoisoned());
            }
        }
    }
}