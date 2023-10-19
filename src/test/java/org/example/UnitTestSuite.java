package org.example;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({CardUnitTests.class, CardGeneratorUnitTests.class, DeckUnitTests.class, PlayerUnitTests.class,
        MeleeUnitTests.class, RoundUnitTests.class, TournamentGameTests.class})
public class UnitTestSuite {
}