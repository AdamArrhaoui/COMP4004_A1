package org.tournamentGame;

public enum CardType {
    BASIC,
    ALCHEMY,
    MERLIN,
    APPRENTICE;

    public static CardType fromCode(String code) {
        for (CardType type : values()) {
            if (type.name().startsWith(code.toUpperCase())) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching CardType for code: " + code);
    }
}
