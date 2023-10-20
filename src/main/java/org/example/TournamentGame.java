package org.example;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Stream;

public class TournamentGame {
    static final int MIN_PLAYERS = 3;
    static final int MAX_PLAYERS = 5;
    private int numPlayers;
    private List<Player> players;

    /**
     * New game. Uses input and output to prompt for number of players and player names.
     * @param input Scanner
     * @param output PrintWriter
     */
    TournamentGame(Scanner input, PrintWriter output){
        promptPlayerCount(input, output);
        setupPlayers(input, output);
    }

    /**
     * New game with given number of players. Does not setup players, must call setupPlayers() after this
     * @param numPlayers number of players
     */
    TournamentGame(Scanner input, PrintWriter output, int numPlayers){
        if (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) throw new IllegalArgumentException("Player count is invalid!");
        this.numPlayers = numPlayers;
        setupPlayers(input, output);
    }

    /**
     * New game with pre-existing list of players
     * @param players list of existing players
     */
    TournamentGame(List<Player> players){
        this.numPlayers = players.size();
        if (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) throw new IllegalArgumentException("Player count is invalid!");
        this.players = players;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setupPlayers(Scanner input, PrintWriter output) {
        players = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            String nameStr = "";
            output.println();
            while (nameStr == null || nameStr.isBlank()){
                output.print("\nPlease enter a name for player %d: ".formatted(i+1));
                output.flush();
                try {
                    nameStr = input.nextLine().strip();
                } catch (Exception e){
                    output.println("\nInput invalid! Please try again\n");
                }
            }
            players.add(new Player(nameStr));
        }
    }

    public void promptPlayerCount(Scanner input, PrintWriter output) {
        int selectedVal = 0;
        while (selectedVal == 0){
            output.print("How many players? (between %d and %d): ".formatted(MIN_PLAYERS, MAX_PLAYERS));
            output.flush();

            String strInput = input.nextLine().replaceAll("\\s", "");
            try {
                int selection = Integer.parseInt(strInput);
                if (selection >= MIN_PLAYERS && selection <= MAX_PLAYERS) {
                    selectedVal = selection;
                }
            } catch (NumberFormatException ignored) {}
            if (selectedVal == 0){
                output.println("\nInvalid input! Please enter a number within range.\n");
            }
        }
        this.numPlayers = selectedVal;
    }

    public void announceResults(PrintWriter output) {
        List<Player> nonLosers = players.stream()
                .filter(player -> player.getHealth()!=0)
                .sorted(Comparator.comparingInt(Player::getHealth).reversed())
                .toList();
        output.println();

        if(nonLosers.isEmpty()){
            output.println("All the players have died! There are no winners!");
        } else {
            int winningHealth = nonLosers.get(0).getHealth();
            List<Player> winners = nonLosers.stream().filter(player -> player.getHealth() == winningHealth).toList();
            if (winners.size() == 1){
                output.println("The winner is %s!".formatted(winners.get(0).getName()));
            } else {
                output.print("The winners are: ");
                for (int i = 0; i < winners.size()-1; i++) {
                    output.print("%s, ".formatted(winners.get(i).getName()));
                }
                output.println("and %s!".formatted(winners.get(winners.size()-1).getName()));
            }
        }
    }
}
