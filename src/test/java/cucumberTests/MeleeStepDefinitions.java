package cucumberTests;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.tournamentGame.Card;
import org.tournamentGame.Melee;
import org.tournamentGame.Player;
import org.tournamentGame.TournamentGame;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;

public class MeleeStepDefinitions {
    private ArrayList<Player> players;
    private Melee currentMelee;
    
    @Given("A single melee is started with players named {string}")
    public void SingleMeleePlayers(String playerNames) {
        players = new ArrayList<>(5);
        for (String name : playerNames.split(",")){
            players.add(new Player(name));
        }
        assertTrue(players.size() >= TournamentGame.MIN_PLAYERS && players.size() <= TournamentGame.MAX_PLAYERS);
        currentMelee = new Melee(players, players.get(0));
    }

    @And("{string} has the card {card}")
    public void playerHasCard(String playerName, Card card) {
        // Find the desired player
        Player desiredPlayer = currentMelee.getPlayers().stream()
                .filter(player -> playerName.equals(player.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("Can't find player named %s!".formatted(playerName), desiredPlayer);
        assertNotNull("Can't add null card to %s's hand!".formatted(playerName), card);

        desiredPlayer.getHand().addCard(card);
    }

    @When("all players play their first card in the melee")
    public void allPlayersPlayTheirFirstCardInTheMelee() {
        assertFalse("Some players don't have a card in their hand!",
                currentMelee.getPlayers().stream()
                        .anyMatch(player -> player.getHand().getCards().isEmpty())
        );

        String input = "1\n".repeat(currentMelee.getPlayers().size());
        StringWriter output = new StringWriter();

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            currentMelee.playCards(new Scanner(input), new PrintWriter(output));
        });
    }
}
