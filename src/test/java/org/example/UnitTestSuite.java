package org.example;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({CardUnitTests.class, DeckUnitTests.class})
public class UnitTestSuite {
}