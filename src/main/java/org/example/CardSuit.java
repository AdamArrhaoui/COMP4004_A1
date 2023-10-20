package org.example;

public enum CardSuit {
    ANY         ("??", "\033[97m"),
    SWORDS      ("SW", "\033[96m"),
    ARROWS      ("AR", "\033[91m"),
    SORCERY     ("SO", "\033[95m"),
    DECEPTION   ("DE", "\033[93m");
    private final String symbol;
    private final String col;
    CardSuit(String symbol, String col){
        this.symbol = symbol;
        this.col = col;
    }
    public String getSymbol(){
        return symbol;
    }
    public String getCol() {
        return col;
    }
}
