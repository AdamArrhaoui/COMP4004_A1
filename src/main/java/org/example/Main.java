package org.example;

import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        PrintWriter output = new PrintWriter(System.out);
        Player.setStartingHealth(50);
        TournamentGame game = new TournamentGame(input, output);
        while (!game.isGameOver()){
            game.playRound(input, output);
        }
        game.announceResults(output);
    }
}