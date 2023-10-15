package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

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

    @Test
    @DisplayName("U-TEST 043: Player can be prompted to select a valid card suit.")
    void testPlayerCardSuitPrompt(){
        // Map of cardsuits to input strings that should result in that suit
        // Testing for no case sensitivity, multiple spellings, and also using the suit index
        Map<CardSuit,List<String>> validSuitStrings = Map.of(
                CardSuit.SWORDS, List.of("Sw", "SWoRds", "SW     ", "sword", "1"),
                CardSuit.ARROWS, List.of(" AR", "ar", "arr ows", "Arrows ", "2"),
                CardSuit.SORCERY, List.of("SO", "sO", " SOr  ", "SOrcery", "3", " 3 "),
                CardSuit.DECEPTION, List.of("DECEPT", "dec", "DE", "dece pt ION  ", " 4")
        );

        // List of invalid inputs. Inputting these should result in a re-prompt
        List<String> invalidSuitStrings = List.of("", "afsefsva", "s", " s", "a", "-1", "0", "ANY", "any", "BASIC", "NaN");

        Player player = new Player("Bobby");
        StringWriter output = new StringWriter();

        for (Map.Entry<CardSuit,List<String>> entry : validSuitStrings.entrySet()){
            CardSuit expectedSuit = entry.getKey();
            List<String> inputStrings = entry.getValue();
            for(String str : inputStrings){
                String input = str + "\n";
                // The prompt should complete almost instantly.
                // If it takes longer than 1 second, it's probably re-prompting, which means test failed.
                CardSuit resultSuit = assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
                    return player.promptCardSuit(new Scanner(input), new PrintWriter(output));
                }, String.format("promptCardSuit timed out! Input parsing probably failed for string '%s'", input));
                assertNotNull(resultSuit);
                assertEquals(expectedSuit, resultSuit,
                        String.format("Wrong card suit returned. \nInput: '%s' \nResult: %s \n Expected: %s",
                                input, resultSuit, expectedSuit));
            }
        }

        // Test invalid inputs correctly re-prompting. Using Sword value as a test
        String invalidInputChain = String.join("\n", invalidSuitStrings);
        CardSuit resultSuit = assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            return player.promptCardSuit(new Scanner(invalidInputChain + "\nSW"), new PrintWriter(output));
        });
        assertNotNull(resultSuit);
        assertEquals(CardSuit.SWORDS, resultSuit);
    }

    @Test
    @DisplayName("U-TEST 044: Player can be prompted to select a valid card value.")
    void testPlayerCardValuePrompt(){
        List<String> validStrings = List.of("1", "01", " 12", "15", "09", "8  ", "  7 ");
        List<String> invalidStrings = List.of("0", "fbase", "", " ", "16", "-1", "100", "null", "NaN", "01   121");

        Player player = new Player("Billy");
        StringWriter output = new StringWriter();

        for (String str : validStrings) {
            String input = str + "\n";
            int result = assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
                return player.promptCardValue(new Scanner(input), new PrintWriter(output));
            });
            assertTrue(result >= 1 && result <= Card.MAX_VALUE);
        }

        // Test invalid inputs correctly re-prompting. Using value 5 as a test
        String invalidInputChain = String.join("\n", invalidStrings);
        int result = assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            return player.promptCardValue(new Scanner(invalidInputChain + "\n5\n"), new PrintWriter(output));
        });
        assertEquals(5, result);
    }

    @Test
    @DisplayName("U-TEST 045: Player can be prompted to fill in a non-basic card's suit or value.")
    void testPlayerCardInfoPrompt(){
        StringWriter output = new StringWriter();

        // Test player gets prompted to change non-basic card suits and/or values
        Player specialPlayer = new Player("Special Sally");
        // List of non-basic cards to test
        List<Card> nonBasicCards = Stream.of(
                CardGenerator.generateAlchemyCards(15),
                CardGenerator.generateCardStream(3, CardType.MERLIN, CardSuit.ANY, 0),
                CardGenerator.generateCardStream(2, CardType.APPRENTICE, CardSuit.ANY, 0)
        ).flatMap(c -> c).toList();

        specialPlayer.getHand().addCards(nonBasicCards);
        specialPlayer.getHand().shuffle();

        for (Card card : specialPlayer.getHand().getCards()) {
            int expectedValue;
            // Test setting card suits to SWORDS
            CardSuit expectedSuit = CardSuit.SWORDS;
            String input;
            if (card.getType() == CardType.ALCHEMY){
                expectedValue = card.getValue();
                input = expectedSuit.toString() + "\n";
            } else {
                expectedValue = 1; // Test card with value of 1
                input = expectedSuit.toString() + "\n" + expectedValue + "\n";
            }
            assertTimeoutPreemptively(Duration.ofSeconds(1), () ->
                specialPlayer.promptFillCardInfo(card, new Scanner(input), new PrintWriter(output))
            );
            assertEquals(expectedValue, card.getValue());
            assertEquals(expectedSuit, card.getSuit());
        }

        // Test that player is not prompted to fill in basic cards (They already have a suit and value)
        Player basicPlayer = new Player("Basic Billy");
        // List of all basic cards to test
        List<Card> basicCards = CardGenerator.generateBasicCards(60, CardSuit.ANY).toList();

        basicPlayer.getHand().addCards(basicCards);
        basicPlayer.getHand().shuffle();

        for (Card card : basicPlayer.getHand().getCards()) {
            int expectedValue = card.getValue();
            CardSuit expectedSuit = card.getSuit();
            String input = "SWORDS\n1\n";
            assertTimeoutPreemptively(Duration.ofSeconds(1), () ->
                    specialPlayer.promptFillCardInfo(card, new Scanner(input), new PrintWriter(output))
            );
            assertEquals(expectedValue, card.getValue());
            assertEquals(expectedSuit, card.getSuit());
        }
    }

    @Test
    @DisplayName("U-TEST 047: Player can be prompted to play the first card of the round, and specify suit/value if needed. Can only play Alchemy if no other card type in hand.")
    void testPlayFirstCard(){
        StringWriter output = new StringWriter();
        Player testPlayer = new Player("Billy");
        // Asking player to play card when they don't have any cards isn't allowed.
        assertThrows(IllegalStateException.class, () -> testPlayer.promptPlayFirstCard(new Scanner("1\n"), new PrintWriter(output)));
        // List of cards to test. 4 Basic, then 2 Merlin, then 2 Apprentice
        List<Card> testCards = Stream.of(
                CardGenerator.generateBasicCards(4, CardSuit.ANY),
                CardGenerator.generateCardStream(2, CardType.MERLIN, CardSuit.ANY, 0),
                CardGenerator.generateCardStream(2, CardType.APPRENTICE, CardSuit.ANY, 0),
                CardGenerator.generateAlchemyCards(2)
                ).flatMap(c -> c).toList();
        testPlayer.getHand().addCards(testCards);

        // Try playing every card in hand in order.
        for (int i = 0; i < testPlayer.getHand().getCards().size(); ++i) {
            Card expectedCard = testPlayer.getHand().getCards().get(i);
            CardSuit expectedSuit;
            int expectedValue;
            String input;

            if (expectedCard.getType() == CardType.ALCHEMY){
                // Test that player cannot select alchemy card while other card types present
                // Try to select this card, then when re-prompted select first (basic) card instead
                input = (i+1) + "\n1\n";
                expectedCard = testPlayer.getHand().getCards().get(0);
                expectedSuit = expectedCard.getSuit();
                expectedValue = expectedCard.getValue();
            } else if (expectedCard.getType() == CardType.BASIC){
                // Basic cards don't require filling in any extra suit/value information
                input = (i+1) + "\n";
                expectedSuit = expectedCard.getSuit();
                expectedValue = expectedCard.getValue();
            } else {
                // Merlin and Apprentice cards require both suit and value to be specified
                expectedSuit = CardSuit.SORCERY;
                expectedValue = 15;
                input = "%d\n%s\n%d\n".formatted(i+1, expectedSuit.toString(), expectedValue);
            }

            Card actualCard = assertTimeoutPreemptively(Duration.ofSeconds(1),
                    () -> testPlayer.promptPlayFirstCard(new Scanner(input), new PrintWriter(output))
            );
            assertNotNull(actualCard);
            assertSame(expectedCard, actualCard);
            assertEquals(actualCard.getSuit(), expectedSuit);
            assertEquals(actualCard.getValue(), expectedValue);
        }

        // Now we should test if alchemy is allowed to be played when no other card present.
        // Player should not be prompted to set its suit because it's the first turn of the round
        Player alchemyPlayer = new Player("Alchemy dude");
        Card alchemyCard1 = new Card(CardType.ALCHEMY, CardSuit.ANY, 1);
        Card alchemyCard2 = new Card(CardType.ALCHEMY, CardSuit.ANY, 2);
        Card basicCard = new Card(CardSuit.SWORDS, 1);
        alchemyPlayer.getHand().addCards(List.of(alchemyCard1, alchemyCard2, basicCard));

        // Ask for first card, expect re-prompt, then ask for our basic card
        String input = "1\n3\n";
        Card actualCard = assertTimeoutPreemptively(Duration.ofSeconds(1),
                () -> alchemyPlayer.promptPlayFirstCard(new Scanner(input), new PrintWriter(output))
        );
        // We expect to have the basic card here
        assertSame(basicCard, actualCard);
        // Remove basic card from hand. The only cards left should be our alchemy cards
        alchemyPlayer.getHand().removeCard(basicCard);
        // Ask for first card, we should get our alchemy card as there are no other card types in the hand
        String newInput = "1\n";
        // We shouldn't time out here because alchemy cards don't need a suit prompt on first round
        actualCard = assertTimeoutPreemptively(Duration.ofSeconds(1),
                () -> alchemyPlayer.promptPlayFirstCard(new Scanner(newInput), new PrintWriter(output))
        );
        assertSame(alchemyCard1, actualCard);
        // Make sure the card suit stays ANY
        assertEquals(CardSuit.ANY, actualCard.getSuit());
    }

    @ParameterizedTest
    @DisplayName("U-TEST 048: Player can be prompted to select a card with a specific suit to play, and select card value if needed. Alchemy card can only be played if player doesn't have any other card type.")
    @EnumSource(value = CardSuit.class)
    void testPromptPlayCard(CardSuit desiredSuit){
        StringWriter output = new StringWriter();

        Player testPlayer = new Player("Bobbert");
        // Asking player to play card when they don't have any cards isn't allowed.
        assertThrows(IllegalStateException.class, () -> testPlayer.promptPlayCard(desiredSuit, new Scanner("1\n"), new PrintWriter(output)));

        List<Card> testCards = List.of(
                new Card(CardSuit.SWORDS, 1),
                new Card(CardSuit.ARROWS, 1),
                new Card(CardSuit.SORCERY, 1),
                new Card(CardSuit.DECEPTION, 1),
                new Card(CardType.ALCHEMY, CardSuit.ANY, 1),
                new Card(CardType.MERLIN, CardSuit.ANY, 0),
                new Card(CardType.APPRENTICE, CardSuit.ANY, 0)
        );
        // Ensure that first card in player's deck is always a valid option
        if (desiredSuit != CardSuit.ANY){
            testPlayer.getHand().addCard(new Card(desiredSuit, 1));
        }
        testPlayer.getHand().addCards(testCards);

        int desiredVal = 1;

        for (int i = 0; i < testPlayer.getHand().getCards().size(); ++i) {
            Card expectedCard = testPlayer.getHand().getCards().get(i);
            String input;

            if (expectedCard.getType() == CardType.ALCHEMY) {
                // Test that player cannot select alchemy card while other card types present
                // Try to select this card, then when re-prompted select first valid basic card instead
                input = (i+1) + "\n1\n";
                expectedCard = testPlayer.getHand().getCards().get(0);
            } else if (expectedCard.getType() == CardType.BASIC){
                if (desiredSuit == CardSuit.ANY || expectedCard.getSuit() == desiredSuit) {
                    // Basic cards don't require filling in any extra suit/value information.
                    input = (i+1) + "\n";
                } else {
                    // This card's suit doesn't match the desired suit
                    // Try to select this basic card, then when re-prompted select first valid basic card instead
                    input = (i+1) + "\n1\n";
                    expectedCard = testPlayer.getHand().getCards().get(0);
                }
            } else {
                // MERLIN and APPRENTICE cards
                if (desiredSuit == CardSuit.ANY){
                    input = "%d\n%s\n%d\n".formatted(i+1, CardSuit.SWORDS, desiredVal);
                } else {
                    // Merlin and Apprentice cards require value to be specified. Suit should be automatically set
                    input = "%d\n%d\n".formatted(i+1, desiredVal);
                }
            }
            Card actualCard = assertTimeoutPreemptively(Duration.ofSeconds(1),
                    () -> testPlayer.promptPlayCard(desiredSuit, new Scanner(input), new PrintWriter(output))
            );
            assertNotNull(actualCard);
            assertSame(expectedCard, actualCard);
            if (desiredSuit != CardSuit.ANY)
                assertEquals(actualCard.getSuit(), desiredSuit);
            assertEquals(actualCard.getValue(), desiredVal);
        }

        // ALCHEMY CARD TESTING
        // Now we should test if alchemy is allowed to be played when no other card present.
        // Player should be prompted to set its suit when suit restriction is ANY
        Player alchemyPlayer = new Player("Alchemy dude");
        Card alchemyCard1 = new Card(CardType.ALCHEMY, CardSuit.ANY, 1);
        Card alchemyCard2 = new Card(CardType.ALCHEMY, CardSuit.ANY, 2);
        alchemyPlayer.getHand().addCards(List.of(alchemyCard1, alchemyCard2));

        String input;
        if (desiredSuit == CardSuit.ANY){
            // Ask for first card, specify desired suit (the suit restriction is ANY, so we need to give a suit).
            input = "1\nSW\n";
        } else {
            // If suit restriction isn't ANY, then the suit should be auto-set
            input = "1\n";
        }
        Card actualCard = assertTimeoutPreemptively(Duration.ofSeconds(1),
                () -> alchemyPlayer.promptPlayCard(desiredSuit, new Scanner(input), new PrintWriter(output))
        );
        // We should get our alchemy card as there are no other card types in the hand card here.
        assertSame(alchemyCard1, actualCard);
        if (desiredSuit == CardSuit.ANY)
            // We specified swords so it should have sword suit
            assertEquals(CardSuit.SWORDS, actualCard.getSuit());
        else
            assertEquals(desiredSuit, actualCard.getSuit());
    }

    @Test
    @DisplayName("U-TEST 049: Player stores the amount of health they currently have. Player health is initially set to Player class initial health.")
    void testPlayerStartingHealth(){
        // Test starting health cannot be set to <= 0
        assertThrows(IllegalArgumentException.class, () -> Player.setStartingHealth(0));
        assertThrows(IllegalArgumentException.class, () -> Player.setStartingHealth(-100));
        Player.setStartingHealth(50);
        Player player = new Player("Billy");
        Player.setStartingHealth(100);
        Player player2 = new Player("Bobby");

        assertEquals(50, player.getHealth());
        assertEquals(100, player2.getHealth());
    }
}