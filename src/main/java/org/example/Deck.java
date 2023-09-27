package org.example;

import java.util.ArrayList;

public class Deck {

    private ArrayList<Card> cards;

    public Deck(){
        cards = new ArrayList<>();
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        if (card == null) throw new IllegalArgumentException("Cannot add null card to deck!");
        cards.add(card);
        card.setDeck(this);
    }
}
