package org.example;

public enum CardSuit {
    ANY         ("??"),
    SWORDS      ("SW"),
    ARROWS      ("AR"),
    SORCERY     ("SO"),
    DECEPTION   ("DE");
    private final String symbol;
    CardSuit(String symbol){
        this.symbol = symbol;
    }
    public String getSymbol(){
        return symbol;
    }
}
