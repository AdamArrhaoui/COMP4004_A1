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

    @Test
    @DisplayName("U-TEST 017: Card added to Deck gets stored in Deck's cardList")
    void testAddCardStoredInDeck(){
        Deck deck = new Deck();
        Card card = new Card(CardSuit.SWORDS, 1);
        deck.addCard(card);
        assertTrue(deck.getCards().contains(card));
        assertEquals(1, deck.getCards().size());
    }

    @Test
    @DisplayName("U-TEST 018: Cannot add null card to Deck")
    void testAddNullCard(){
        Deck deck = new Deck();
        assertThrows(IllegalArgumentException.class ,() ->deck.addCard(null));
        assertEquals(0, deck.getCards().size());
    }

    @Test
    @DisplayName("U-TEST 019: Adding card to Deck updates the card's Deck reference to the new Deck")
    void testCardDeckReferenceUpdateOnAdd(){
        Deck deck = new Deck();
        Card card = new Card(CardSuit.SWORDS, 1);
        deck.addCard(card);
        assertNotNull(card.getDeck());
        assertEquals(deck, card.getDeck());
    }
}