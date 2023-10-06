package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PlayerUnitTests {
    @ParameterizedTest
    @DisplayName("U-TEST 036: New player has non-blank name")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "Billy", "Bobby", "Sammy"})
    void testNewPlayerNonEmptyName(String testName){
        if (testName == null || testName.isBlank()){
            assertThrows(IllegalArgumentException.class, () -> new Player(testName));
            return;
        }
        Player player = new Player(testName);
        assertEquals(testName, player.getName());
    }
}