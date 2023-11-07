package org.tournamentGame;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Card {
    static final int MIN_VALUE = 1;
    static final int MAX_VALUE = 15;
    static final Map<CardSuit, Set<Integer>> POISON_VALUES = Map.of(
            CardSuit.SWORDS, Set.of(6, 7, 8, 9),
            CardSuit.ARROWS, Set.of(8, 9, 10, 11),
            CardSuit.SORCERY, Set.of(5, 6, 11, 12),
            CardSuit.DECEPTION, Set.of(6, 7, 9, 10)
    );

    private CardType type;
    private CardSuit suit;
    private int value;
    private Deck deck;

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

    @Override
    public String toString(){
        return String.format("""
                ╭────╮
                │%2.2s  │
                │ %s%2.2s\033[0m │
                │  %02d│
                ╰────╯
                """, type.toString(), suit.getCol(), suit.getSymbol(), value);
    }

    public static String listToString(Collection<Card> cards){
        if(cards.isEmpty()) return "";
        List<String[]> cardStrings = cards.stream()
                .map(Card::toString)
                .map(str -> str.split("\n"))
                .toList();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < cardStrings.get(0).length; i++) {  // assuming all cards have same number of lines
            for (String[] cardString : cardStrings) {
                result.append(cardString[i]);
            }
            result.append("\n");
        }
        return result.toString().strip();
    }

    public boolean cardEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return getValue() == card.getValue() && getType() == card.getType() && getSuit() == card.getSuit();
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
        if (this.type == CardType.BASIC){
            throw new IllegalStateException("Basic cards cannot have their suit changed!");
        }
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
        if (this.type == CardType.BASIC || this.type == CardType.ALCHEMY){
            throw new IllegalStateException("Basic and Alchemy cards cannot have their value changed!");
        }
        setValue(value);
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck newDeck){
        this.deck = newDeck;
    }

    public boolean isPoisoned(){
        if (this.type == CardType.BASIC){
            return POISON_VALUES.get(this.suit).contains(this.value);
        }
        return false;
    }

    public int getInjuryPoints(){
        switch (type){
            case BASIC:
                if(isPoisoned()){
                    return 10;
                } else {
                    return 5;
                }
            case ALCHEMY, APPRENTICE:
                return 5;
            case MERLIN:
                return 25;
            default:
                return 0;
        }
    }
}
