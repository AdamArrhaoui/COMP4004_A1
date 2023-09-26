package org.example;

public class Card {
    static final int MIN_VALUE = 1;
    static final int MAX_VALUE = 15;

    private CardType type;
    private CardSuit suit;
    private int value;

    Card(CardType t, CardSuit s, int val){
        setType(t);
        setSuit(s);
        setValue(val);
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        if (type == null){
            throw new IllegalArgumentException("CardType cannot be null!");
        }
        this.type = type;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public void setSuit(CardSuit suit) {
        if (suit == null){
            throw new IllegalArgumentException("CardSuit cannot be null!");
        }
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value != 0){
            if(value < MIN_VALUE || value > MAX_VALUE){
                throw new IllegalArgumentException("Cannot set card value out of allowed range!");
            }
        }
        this.value = value;
    }

}
