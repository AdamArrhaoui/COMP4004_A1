package org.example;

import java.util.ArrayList;

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
        return new Deck();
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

    public void removeCard(Card card) {
        cards.remove(card);
        card.setDeck(null);
    }
}
