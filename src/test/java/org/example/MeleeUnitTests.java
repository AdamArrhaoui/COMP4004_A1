package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MeleeUnitTests {

    static List<Player> players;
    static Random random;

    @BeforeAll
    static void setupPlayers(){
        players = List.of(
                new Player("Billy"),
                new Player("Bobby"),
                new Player("Sammy"),
                new Player("Freddy"),
                new Player("Bobbington")
        );
        random = new Random();
    }

    @BeforeEach
    void setupCards(){
        for (Player player: players) {
            player.getHand().removeAllCards();
            player.getInjuryDeck().removeAllCards();
        }
    }

    private static Stream<Integer> providePlayerIndices(){
        return IntStream.range(0, players.size()).boxed();
    }

    @Test
    @DisplayName("U-TEST 053: Melee class created from ordered list of Players, and player as melee leader. Melee leader must be in player list.")
    void testMeleeCreation(){
        Player sussyPlayer = new Player("imposter");
        // Can't make player not in player list the melee leader
        assertThrows(IllegalArgumentException.class, () -> new Melee(players, sussyPlayer));

        Player expectedLeader = players.get(0);
        Melee melee = new Melee(players, expectedLeader);
        assertArrayEquals(players.toArray(), melee.getPlayers().toArray());
        assertSame(expectedLeader, melee.getPlayerLeader());
    }

    @ParameterizedTest
    @DisplayName("U-TEST 054: Melee class can prompt the leader to play the first card which will determine the melee's suit.")
    @EnumSource(value = CardSuit.class)
    void testLeaderPlayFirstCard(CardSuit expectedSuit){
        for (Player player: players) {
            player.getHand().addCard(new Card(CardType.ALCHEMY, expectedSuit, 1));
        }
        Melee melee = new Melee(players, players.get(0));

        String input = "1\n";
        StringWriter output = new StringWriter();

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> melee.playFirstCard(new Scanner(input), new PrintWriter(output)));
        assertEquals(expectedSuit, melee.getMeleeSuit());
    }

    @ParameterizedTest
    @DisplayName("U-TEST 055: Melee class can prompt all players to play cards, in order starting with the leader, and add the played cards to playedCards list.")
    @MethodSource("providePlayerIndices")
    void testPlayMeleeCards(int leadPlayerIndex){
        for (Player player: players) {
            player.getHand().addCard(new Card(CardSuit.SWORDS, players.indexOf(player) + 1));
        }
        Melee melee = new Melee(players, players.get(leadPlayerIndex));

        String input = "1\n".repeat(players.size());
        StringWriter output = new StringWriter();

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> melee.playCards(new Scanner(input), new PrintWriter(output)));
        assertEquals(players.size(), melee.getPlayedCards().size());

        // Make sure that the cards are played in order starting from the leader.
        for (int i = 0; i < melee.getPlayedCards().size(); i++){
            int currPlayerIndex = (melee.getLeaderIndex() + i) % players.size();
            Player currPlayer = players.get(currPlayerIndex);
            Card expectedCard = currPlayer.getHand().getCards().get(0);
            Card currCard = melee.getPlayedCards().get(i);
            assertSame(expectedCard, currCard);
        }
    }

    @Test
    @DisplayName("U-TEST 056: If a player cannot play a valid card during a melee, they are asked to discard a card and suffer immediate shame damage.")
    void testPlayerShame(){
        // Choose a random player to be shameful (other than first player who is the leader)
        Player shamedPlayer = players.get(random.nextInt(1, players.size()));
        for (Player player: players) {
            CardSuit cardSuit = CardSuit.ARROWS;
            if (player == shamedPlayer){
                // Make shamed player have incompatible suit
                cardSuit = CardSuit.SWORDS;
            }
            player.getHand().addCard(new Card(cardSuit, players.indexOf(player) + 1));
        }

        Melee melee = new Melee(players, players.get(0));
        String input = "1\n".repeat(players.size() + 1); // add 1 extra input for the shamed player discarding their card
        StringWriter output = new StringWriter();

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> melee.playCards(new Scanner(input), new PrintWriter(output)));
        // playedCards size should be 1 less because shamed player doesn't play a card
        assertEquals(players.size() - 1, melee.getPlayedCards().size());

        // Make sure shamed player's hand is empty, and their health went down by SHAME_DAMAGE amount
        assertTrue(shamedPlayer.getHand().getCards().isEmpty());
        assertEquals(Player.getStartingHealth() - Melee.SHAME_DAMAGE, shamedPlayer.getHealth());
    }

    @Test
    @DisplayName("U-TEST 057: Melee class can perform the feint step and ignore all played cards with equal values, and return a list of non-feinted cards.")
    void testCardFeint(){

        // Create 6 card deck for testing. Basic cards with same suit, 3 pairs of the same value, so there will always be 1 left over that isn't feinted
        Deck testDeck = new Deck();
        testDeck.addCards(CardGenerator.generateCardStream(2, CardType.BASIC, CardSuit.SWORDS, 1).toList());
        testDeck.addCards(CardGenerator.generateCardStream(2, CardType.BASIC, CardSuit.SWORDS, 2).toList());
        testDeck.addCards(CardGenerator.generateCardStream(2, CardType.BASIC, CardSuit.SWORDS, 3).toList());
        testDeck.shuffle();

        for (Player player: players) {
            testDeck.dealCardsTo(player.getHand(), 1);
        }
        Card leftoverCard = testDeck.getCards().get(0);
        int leftoverValue = leftoverCard.getValue();

        Melee melee = new Melee(players, players.get(0));
        String input = "1\n".repeat(5); // All 5 players choose the first card
        StringWriter output = new StringWriter();

        // Cant feint before players play their cards
        assertThrows(IllegalStateException.class, melee::feintStep);

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> melee.playCards(new Scanner(input), new PrintWriter(output)));
        List<Card> nonFeintCards = melee.feintStep();

        assertNotNull(nonFeintCards);
        assertEquals(1, nonFeintCards.size());
        assertEquals(leftoverValue, nonFeintCards.get(0).getValue());
    }

    @ParameterizedTest
    @DisplayName("U-TEST 058: Melee class can determine the loser of a melee, which is the player who played the lowest non-feinted card.")
    @MethodSource("providePlayerIndices")
    void testGetMeleeLoser(int loserIndex){

        // Choose player to lose
        Player loserPlayer = players.get(loserIndex);

        int cardVal = 2;
        for (Player player: players) {
            if (player == loserPlayer){
                // Make loser player have lowest cardVal
                cardVal = 1;
            }
            player.getHand().addCard(new Card(CardSuit.SWORDS, cardVal));
            cardVal++;
        } // Cardvals can repeat if the loserIndex is not the first or last player. We want this behaviour to test feinting

        Melee melee = new Melee(players, players.get(0));
        String input = "1\n".repeat(5); // All 5 players choose the first card
        StringWriter output = new StringWriter();

        // Cant determine loser before players play their cards
        assertThrows(IllegalStateException.class, melee::determineLoser);

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> melee.playCards(new Scanner(input), new PrintWriter(output)));
        Player actualLoser = melee.determineLoser();
        assertNotNull(actualLoser);
        assertSame(loserPlayer, actualLoser);
    }

    @Test
    @DisplayName("U-TEST 059: If all player's cards got feinted, the melee will determine that there is no loser (null)")
    void testAllCardsFeintedNoLoser(){
        // Make deck of 5 cards, 2 with the value of 1, and 3 with the value of 2. All these cards should feint each other when they are played
        Deck testDeck = new Deck();
        testDeck.addCards(CardGenerator.generateCardStream(2, CardType.BASIC, CardSuit.SWORDS, 1).toList());
        testDeck.addCards(CardGenerator.generateCardStream(3, CardType.BASIC, CardSuit.SWORDS, 2).toList());
        testDeck.shuffle();

        for (Player player: players) {
            testDeck.dealCardsTo(player.getHand(), 1);
        }

        Melee melee = new Melee(players, players.get(0));
        String input = "1\n".repeat(5); // All 5 players choose the first card
        StringWriter output = new StringWriter();

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> melee.playCards(new Scanner(input), new PrintWriter(output)));

        Player actualLoser = assertDoesNotThrow(melee::determineLoser);
        assertNull(actualLoser);
    }
}