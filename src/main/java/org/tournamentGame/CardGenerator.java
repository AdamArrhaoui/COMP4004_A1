package org.tournamentGame;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CardGenerator {
    /**
     * Generates a stream of distinct cards of given type, suit, and value
     * @param n number of cards in stream
     * @param type CardType of cards in stream
     * @param suit CardSuit of cards in stream
     * @param value Value of cards in stream
     * @return Stream<Card> of new cards
     */
    public static Stream<Card> generateCardStream(int n, CardType type, CardSuit suit, int value){
        CardSupplier supplier = new CardSupplier(type, suit, value, false, false);
        return Stream.generate(supplier).limit(n);
    }

    public static Stream<Card> generateBasicCards(int n, CardSuit suit){
        boolean iterSuit = false;
        if (suit == CardSuit.ANY){
            suit = CardSuit.SWORDS;
            iterSuit = true;
        }
        CardSupplier supplier = new CardSupplier(CardType.BASIC, suit, 1, true, iterSuit);
        return Stream.generate(supplier).limit(n);
    }

    public static Stream<Card> generateAlchemyCards(int n){
        CardSupplier supplier = new CardSupplier(CardType.ALCHEMY, CardSuit.ANY, 1, true, false);
        return Stream.generate(supplier).limit(n);
    }

    public static Stream<Card> generateAllGameCards() {
        return Stream.of(
                generateBasicCards(60, CardSuit.ANY),
                generateAlchemyCards(15),
                generateCardStream(3, CardType.MERLIN, CardSuit.ANY, 0),
                generateCardStream(2, CardType.APPRENTICE, CardSuit.ANY, 0)
        ).flatMap(c -> c);
    }

    private static class CardSupplier implements Supplier<Card> {
        // Attributes to keep track of the current card type and value.
        protected final List<CardSuit> SUITLIST = EnumSet.complementOf(EnumSet.of(CardSuit.ANY)).stream().toList();
        private CardType currentType;
        private CardSuit currentSuit;
        private int currentValue;
        private boolean iterValue;
        private boolean iterSuit;

        public CardSupplier(CardType currentType, CardSuit currentSuit, int currentValue, boolean iterValue, boolean iterSuit) {
            this.currentType = currentType;
            this.currentSuit = currentSuit;
            this.currentValue = currentValue;
            this.iterValue = iterValue;
            this.iterSuit = iterSuit;
        }

        @Override
        public Card get() {
            // Create a new card with the current type and value.
            Card card =  new Card(currentType, currentSuit, currentValue);

            if (iterValue)
                nextCardValue();
            if (iterSuit)
                nextCardSuit();

            return card;
        }
        private void nextCardValue(){
            if (++currentValue > Card.MAX_VALUE) {
                currentValue = Card.MIN_VALUE;
            }
        }
        private void nextCardSuit(){
            if (currentValue <= Card.MIN_VALUE){
                int newIndex = (SUITLIST.indexOf(currentSuit) + 1) % SUITLIST.size();
                currentSuit = SUITLIST.get(newIndex);
            }
        }
    }
}
