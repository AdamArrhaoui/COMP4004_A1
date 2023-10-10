package org.example;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringJoiner;

public class Player {
    private String name;
    private Deck hand;
    private Deck injuryDeck;

    Player(String name){
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("New player name cannot be null or blank!");
        }
        this.name = name;
        this.hand = new Deck(this);
        this.injuryDeck = new Deck();
    }

    public String getName() {
        return name;
    }

    public Deck getHand() {
        return hand;
    }

    public Deck getInjuryDeck() {
        return injuryDeck;
    }

    public Card promptAnyCard(Scanner input, PrintWriter output) {
        if (hand.getCards().isEmpty()) return null;
        // Build string that marks 1-based card indices for the user to choose.
        StringJoiner stringJoiner = new StringJoiner(" || ", "| ", " |");
        for (int i = 1; i <= hand.getCards().size(); i++) {
            // Fancy escape characters to make the numbers in the terminal highlighted purple
            stringJoiner.add(String.format("\033[32m%-2d\033[0m", i));
        }

        int selectedIdx = 0;
        while (selectedIdx == 0){
            output.println(hand.getCardsString());
            output.println(stringJoiner.toString());
            output.print("Select a card index: ");
            int intInput = 0;
            try{
                String inputStr = input.nextLine().strip();
                intInput = Integer.parseInt(inputStr);
                if (intInput < 1 || intInput > hand.getCards().size()) throw new IndexOutOfBoundsException();
            } catch (Exception e) {
                output.printf("\nInvalid input! Please enter a number between 1 and %d\n", hand.getCards().size());
                continue;
            } finally {
                output.println();
            }
            selectedIdx = intInput;
        }
        return hand.getCards().get(selectedIdx - 1);
    }

    public Card promptDiscardCard(Scanner input, PrintWriter output) {
        if (hand.getCards().isEmpty()) return null;
        Card cardToDiscard = promptAnyCard(input, output);
        hand.removeCard(cardToDiscard);
        return cardToDiscard;
    }

    public CardSuit promptCardSuit(Scanner input, PrintWriter output) {
        return null;
    }
}
