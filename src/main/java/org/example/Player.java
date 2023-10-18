package org.example;

import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.StringJoiner;

public class Player {
    static final int DEFAULT_STARTING_HEALTH = 100;
    private String name;
    private Deck hand;
    private Deck injuryDeck;
    private int health;
    private static int startingHealth = DEFAULT_STARTING_HEALTH;

    Player(String name){
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("New player name cannot be null or blank!");
        }
        this.name = name;
        this.hand = new Deck(this);
        this.injuryDeck = new Deck();
        this.health = startingHealth;
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public static int getStartingHealth() {
        return startingHealth;
    }

    public static void setStartingHealth(int startingHealth) {
        if (startingHealth <= 0) throw new IllegalArgumentException("Starting health must be greater than 0");
        Player.startingHealth = startingHealth;
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

    public Integer promptCardValue(Scanner input, PrintWriter output) {
        int selectedVal = 0;
        while (selectedVal == 0){
            output.print("Select a card value (between %d and %d): ".formatted(Card.MIN_VALUE, Card.MAX_VALUE));
            output.flush();

            String strInput = input.nextLine().replaceAll("\\s", "");
            try {
                int selection = Integer.parseInt(strInput);
                if (selection >= Card.MIN_VALUE && selection <= Card.MAX_VALUE) {
                    selectedVal = selection;
                }
            } catch (NumberFormatException ignored) {}
            if (selectedVal == 0){
                output.println("\nInvalid input! Please enter a number within range.\n");
            }
        }
        return selectedVal;
    }

    public void promptFillCardInfo(Card card, Scanner input, PrintWriter output){
        // Only prompt card's suit if the card's suit is ANY
        if (card.getSuit() == CardSuit.ANY){
            CardSuit newSuit = promptCardSuit(input, output);
            card.setSuit(newSuit);
        }
        if (card.getValue() == 0){
            int newValue = promptCardValue(input, output);
            card.setValue(newValue);
        }
    }

    public Card promptPlayCard(CardSuit suitRestriction, Scanner input, PrintWriter output){
        if (hand.getCards().isEmpty()) throw new IllegalStateException("Player's hand is empty!");

        if (suitRestriction != CardSuit.ANY){
            boolean canPlaySuit = hand.containsSuit(CardSuit.ANY) || hand.containsSuit(suitRestriction);
            if (!canPlaySuit){
                output.println("You can't play any %s cards!".formatted(suitRestriction.toString()));
                return null;
            }
        }

        boolean containsNonAlchemy = hand.containsNonAlchemy();
        Card chosenCard = null;
        while (chosenCard == null){
            output.println("Choose a card with %s suit:".formatted(suitRestriction.toString()));
            chosenCard = promptAnyCard(input, output);

            if (chosenCard.getType() == CardType.ALCHEMY && containsNonAlchemy) {
                output.println("\nYou must choose non-alchemy card if you have one!\n");
                chosenCard = null;
                continue;
            }
            if (suitRestriction != CardSuit.ANY) {
                if (chosenCard.getType() == CardType.BASIC && chosenCard.getSuit() != suitRestriction){
                    output.println("\nThat basic card is the wrong suit! You need to play a card with %s suit!\n".formatted(suitRestriction.toString()));
                    chosenCard = null;
                    continue;
                }
                chosenCard.setSuit(suitRestriction);
            }
            promptFillCardInfo(chosenCard, input, output);
        }
        return chosenCard;
    }

    public Card promptPlayFirstCard(Scanner input, PrintWriter output) {
        if (hand.getCards().isEmpty()) throw new IllegalStateException("Player's hand is empty!");

        Card cardToPlay = null;
        while (cardToPlay == null){
            output.println("Choose the first card to play!");
            Card chosenCard = promptAnyCard(input, output);
            if (chosenCard.getType() == CardType.ALCHEMY){
                if (hand.containsNonAlchemy()){
                    output.println("\nYou can't choose an Alchemy card when you have non-Alchemy cards in your hand!\n");
                    continue;
                }
                // Don't ask to fill card info for alchemy card. Keep it as ANY
                cardToPlay = chosenCard;
            } else {
                promptFillCardInfo(chosenCard, input, output);
                cardToPlay = chosenCard;
            }
        }
        return cardToPlay;
    }

    public void takeDamage(int damage) {
        if (damage < 0) throw new IllegalArgumentException("Cannot take negative damage!");
        health = Math.max(0, health-damage);
    }

    public void takeInjuryDeckDamage() {
        if (injuryDeck.getCards().isEmpty()) return;
        int amountInjury = injuryDeck.getTotalInjury();
        takeDamage(amountInjury);
        injuryDeck.removeAllCards();
    }
}
