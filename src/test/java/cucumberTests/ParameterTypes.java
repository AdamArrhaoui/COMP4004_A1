package cucumberTests;

import io.cucumber.java.ParameterType;
import org.tournamentGame.Card;
import org.tournamentGame.CardSuit;
import org.tournamentGame.CardType;

public class ParameterTypes {

    @ParameterType("(?<Type>[A-Za-z]{2})?(?<Suit>[A-Za-z]{2})(?<Value>\\d{1,2})")
    public Card card(String typeString, String suitString, String valueString) {
        CardType cardType = (typeString != null) ? CardType.fromCode(typeString) : CardType.BASIC;
        CardSuit cardSuit = CardSuit.fromCode(suitString);
        int cardValue = Integer.parseInt(valueString);

        return new Card(cardType, cardSuit, cardValue);
    }
}
