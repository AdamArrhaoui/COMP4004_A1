package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    @DisplayName("U-TEST 020: Card removed from Deck gets removed from Deck's cardList")
    void testCardRemovedFromCardList(){
        Deck deck = new Deck();
        Card card = new Card(CardSuit.SWORDS, 1);
        deck.addCard(card);
        deck.removeCard(card);
        assertFalse(deck.getCards().contains(card));
        assertEquals(0, deck.getCards().size());
    }

    @Test
    @DisplayName("U-TEST 021: Removing card from Deck resets the card's Deck reference to null")
    void testCardDeckReferenceNullOnRemove(){
        Deck deck = new Deck();
        Card card = new Card(CardSuit.SWORDS, 1);
        deck.addCard(card);
        deck.removeCard(card);
        assertNull(card.getDeck());
    }

    @Test
    @DisplayName("U-TEST 022: Card from existing Deck gets removed from old Deck before adding to new Deck")
    void testCardAddMoveBetweenDecks(){
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        Card card = new Card(CardSuit.SWORDS, 1);
        deck1.addCard(card);
        deck2.addCard(card);
        assertFalse(deck1.getCards().contains(card));
        assertTrue(deck2.getCards().contains(card));
        assertEquals(deck2, card.getDeck());
    }

    @Test
    @DisplayName("U-TEST 023: New Deck created from list of cards contains all given cards.")
    void testDeckConstructorFromCardList(){
        List<Card> cards = new ArrayList<Card>(List.of(
                new Card(CardSuit.SWORDS, 1),
                new Card(CardSuit.ARROWS, 2),
                new Card(CardSuit.SORCERY, 3)
        ));

        Deck deck = new Deck(cards);
        assertArrayEquals(cards.toArray(), deck.getCards().toArray());
        for (Card card: cards) {
            assertSame(deck, card.getDeck());
        }
    }

    @Test
    @DisplayName("U-TEST 029: New full deck contains every game card exactly once")
    void testFullDeckCards(){
        Deck deck = Deck.FullDeck();
        assertEquals(80, deck.getCards().size());
        List<Card> allCards = CardGenerator.generateAllGameCards().toList();
        for (int i = 0; i < 80; i++) {
            assertTrue(allCards.get(i).cardEquals(deck.getCards().get(i)));
        }
    }
}