package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private ArrayList<Card> cards;

    public Deck(){
        cards = new ArrayList<>();
    }

    public Deck(Iterable<Card> cardIterable){
        this();
        cardIterable.forEach(this::addCard);
    }

    public static Deck FullDeck(){
        return new Deck(CardGenerator.generateAllGameCards().toList());
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        if (card == null) throw new IllegalArgumentException("Cannot add null card to deck!");
        Deck oldDeck = card.getDeck();
        if (oldDeck != null){
            oldDeck.removeCard(card);
        }
        cards.add(card);
        card.setDeck(this);
    }

    public void addCards(Iterable<Card> cards){
        for (Card card :
                cards) {
            addCard(card);
        }
    }

    public void removeCard(Card card) {
        cards.remove(card);
        card.setDeck(null);
    }

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public void dealCardsTo(Deck otherDeck, int numCards) {
        if (otherDeck == null){
            throw new NullPointerException("Cannot deal cards to null deck!");
        }
        if (numCards <= 0 || numCards > cards.size()){
            throw new IllegalArgumentException("Attempt to deal invalid number of cards!");
        }
        int numRemoved = 0;
        List<Card> cardsToMove = new ArrayList<>(cards.subList(cards.size() - numCards, cards.size()));
        otherDeck.addCards(cardsToMove);
    }
}
