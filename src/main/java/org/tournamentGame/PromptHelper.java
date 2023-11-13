package org.tournamentGame;

import java.io.PrintWriter;
import java.util.Scanner;

public class PromptHelper {
    public Scanner getInput() {
        return input;
    }

    public void setInput(Scanner input) {
        this.input = input;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public void setOutput(PrintWriter output) {
        this.output = output;
    }

    private Scanner input;
    private PrintWriter output;

    public PromptHelper(Scanner input, PrintWriter output) {
        this.input = input;
        this.output = output;
    }

    public PromptHelper(){
        input = new Scanner(System.in);
        output = new PrintWriter(System.out);
    }

    /**
     * Prompts for a positive integer between 2 numbers. Keeps prompting until valid integer is given
     * @param promptMessage Message that will be outputted to prompt the user, followed by the allowed range
     * @param fromInclusive Minimum valid value (inclusive)
     * @param toInclusive Maximum allowed value (inclusive)
     * @return The int value that the user chooses
     */
    public int promptPositiveInt(String promptMessage, int fromInclusive, int toInclusive){
        if (fromInclusive > toInclusive || fromInclusive < 0)
            throw new IllegalArgumentException("Prompt value range must be ordered positive integers! The range [%d - %d] is invalid!".formatted(fromInclusive, toInclusive));
        int selectedVal = -1;
        while (selectedVal == -1){
            output.print(promptMessage.stripTrailing());
            output.print(" (between %d and %d): ".formatted(fromInclusive, toInclusive));
            output.flush();

            String strInput = input.nextLine().replaceAll("\\s", "");
            try {
                int selection = Integer.parseInt(strInput);
                if (selection >= fromInclusive && selection <= toInclusive) {
                    selectedVal = selection;
                }
            } catch (NumberFormatException ignored) {}
            if (selectedVal == -1){
                output.println("\nInvalid input! Please enter a number within range.\n");
            }
        }
        return selectedVal;
    }
}
