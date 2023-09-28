package org.example;

import java.util.concurrent.atomic.AtomicInteger;
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
        return Stream.generate(() -> new Card(type, suit, value)).limit(n);
    }

    public static Stream<Card> generateBasicCards(int n, CardSuit suit){
        AtomicInteger i = new AtomicInteger();
        return Stream.generate(() -> new Card(suit, i.getAndIncrement()%Card.MAX_VALUE + 1)).limit(n);
    }
}
