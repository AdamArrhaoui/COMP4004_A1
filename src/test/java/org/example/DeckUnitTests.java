package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    @DisplayName("U-TEST 030: Deck can shuffle card order")
    void testDeckShuffle(){
        Deck deck = Deck.FullDeck();
        ArrayList<Card> originalCardArray = new ArrayList<Card>(deck.getCards());
        assertEquals(originalCardArray, deck.getCards());
        deck.shuffle();
        // This test has a 1/(80!) chance of failing by accident. If this test fails and the code is correct, buy a lotto ticket
        assertNotEquals(originalCardArray, deck.getCards());
    }

    @ParameterizedTest
    @DisplayName("U-TEST 031: Deck can draw and deal certain number of cards from back of deck to other Deck")
    @ValueSource(ints = {-1, 0, 10, 30, 100})
    void testDeckDrawAndDeal(int numCards){
        Deck fullDeck = Deck.FullDeck();
        Deck newDeck = new Deck();
        int fullDeckSize = fullDeck.getCards().size();
        if (numCards <= 0 || numCards > fullDeck.getCards().size()){
            // Error handling if numCards <= 0 or numCards > number of cards in original deck
            assertThrows(IllegalArgumentException.class, () -> fullDeck.dealCardsTo(newDeck, numCards));
            return;
        } else {
            // Error handling if dealing to null deck
            assertThrows(NullPointerException.class, () -> fullDeck.dealCardsTo(null, numCards));
        }

        fullDeck.shuffle();
        fullDeck.dealCardsTo(newDeck, numCards);

        assertEquals(numCards, newDeck.getCards().size());
        assertEquals(fullDeckSize - numCards, fullDeck.getCards().size());

        // Test deck transferred properly
        for (Card card : newDeck.getCards()) {
            assertSame(newDeck, card.getDeck());
            // If card in new deck is found in old deck, make sure that the card isn't the same in memory
            // (could be same value but can't be same card reference)
            int cardIdx = fullDeck.getCards().indexOf(card);
            if (cardIdx != -1){
                assertNotSame(card, fullDeck.getCards().get(cardIdx));
            }
        }
    }

    @Test
    @DisplayName("U-TEST 032: Deck can be searched for a card with specific type, suit, and value")
    void testDeckSpecificCardSearch(){
        List<Card> testCards = List.of(
                new Card(CardSuit.SORCERY, 2),
                new Card(CardSuit.SWORDS, 3),
                new Card(CardType.ALCHEMY, CardSuit.SWORDS, 1),
                new Card(CardType.MERLIN, CardSuit.ANY, 10),
                new Card(CardType.APPRENTICE, CardSuit.DECEPTION, 0)
        );
        Deck deck = new Deck(testCards);

        // Test if cards in deck are correctly found
        for (Card card :
                testCards) {
            Card resultCard = deck.findSpecificCard(card.getType(), card.getSuit(), card.getValue());
            assertNotNull(resultCard);
            assertSame(card, resultCard);
        }

        // Test if cards not in deck cannot be found (returns null)
        List<Card> differentCards = List.of(
                new Card(CardType.MERLIN, CardSuit.DECEPTION, 10),
                new Card(CardSuit.SORCERY, 1),
                new Card(CardType.APPRENTICE, CardSuit.ANY, 0)
        );
        for (Card card :
                differentCards) {
            assertNull(deck.findSpecificCard(card.getType(), card.getSuit(), card.getValue()));
        }
    }

    @ParameterizedTest
    @DisplayName("U-TEST 033: Deck can check if it contains any card of a specific Type")
    @EnumSource(
            value = CardType.class,
            mode = EnumSource.Mode.MATCH_ALL
    )
    void testDeckContainsType(CardType testType){
        // Basic deck of cards, one of each card type. All same suit and value
        CardSuit testSuit = CardSuit.SWORDS;
        int testVal = 1;
        List<Card> testCards = List.of(
                new Card(CardType.BASIC, testSuit, testVal),
                new Card(CardType.ALCHEMY, testSuit, testVal),
                new Card(CardType.MERLIN, testSuit, testVal),
                new Card(CardType.APPRENTICE, testSuit, testVal)
        );
        Deck testDeck = new Deck(testCards);
        testDeck.shuffle();
        assertTrue(testDeck.containsType(testType));
        Card cardToRemove = testDeck.findSpecificCard(testType, testSuit, testVal);
        testDeck.removeCard(cardToRemove);
        assertFalse(testDeck.containsType(testType));
    }
}