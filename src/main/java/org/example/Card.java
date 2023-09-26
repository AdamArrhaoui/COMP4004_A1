package org.example;

public class Card {
    static final int MIN_VALUE = 1;
    static final int MAX_VALUE = 15;

    private CardType type;
    private CardSuit suit;
    private int value;

    /**
     * Creates new BASIC card with suit and value
     * @param s non-ANY suit
     * @param val non-zero value
     */
    Card(CardSuit s, int val) {
        setType(CardType.BASIC);
        setSuit(s);
        setValue(val);
    }

    /**
     * Creates new card with given type, suit, and value
     * @param t non-null CardType
     * @param s non-null CardSuit
     * @param val card value
     */
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
        if (this.type == CardType.BASIC && suit == CardSuit.ANY){
            throw new IllegalArgumentException("Basic cards must have non-ANY suit!");
        }
        this.suit = suit;
    }

    public void changeSuit(CardSuit suit){
        setSuit(suit);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {

        if (value != 0){
            if(value < MIN_VALUE || value > MAX_VALUE){
                throw new IllegalArgumentException("Cannot set card value out of allowed range!");
            }
        } else if (this.type == CardType.BASIC){
            throw new IllegalArgumentException("Basic cards cannot have a value of 0!");
        } else if (this.type == CardType.ALCHEMY){
            throw new IllegalArgumentException("Alchemy cards cannot have a value of 0!");
        }
        this.value = value;
    }

    public void changeValue(int value){
        setValue(value);
    }
}
