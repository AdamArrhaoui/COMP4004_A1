package org.example;

import java.io.PrintWriter;
import java.util.EnumSet;
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
        // Build and display string with choices of card suits
        int choiceWidth = 10;
        //String formatString = "(%d) %-" + choiceWidth + "s";
        String formatString = "(\033[32m%d\033[0m) %s";
        StringJoiner promptStringJoiner = new StringJoiner(" | ", "| ", " |");
        EnumSet<CardSuit> suitOptionSet = EnumSet.complementOf(EnumSet.of(CardSuit.ANY));
        suitOptionSet.forEach(suit -> promptStringJoiner.add(formatString.formatted(suit.ordinal(), suit.toString())));

        CardSuit selectedSuit = null;
        while (selectedSuit == null){
            output.println("Select a suit (type its name or number): ");
            output.println(promptStringJoiner);
            output.print("Your choice: ");
            output.flush();

            String strInput = input.nextLine().replaceAll("\\s", "").toUpperCase();

            // Check for ordinal (index) match
            try {
                int index = Integer.parseInt(strInput);
                if (index > 0 && index < CardSuit.values().length) {  // 0 index is for ANY, which is not a valid choice here.
                    selectedSuit = CardSuit.values()[index];
                }
            } catch (NumberFormatException e) {
                // Check for name match
                if (strInput.length() > 1){
                    for (CardSuit suit : suitOptionSet) {
                        if (suit.toString().startsWith(strInput)) {
                            selectedSuit = suit;
                            break;
                        }
                    }
                }
            }
            if (selectedSuit == null)
                output.println("\nInvalid input! Please enter at least the first 2 characters of the suit's name, or the suit's number\n");
        }
        return selectedSuit;
    }
}
