package com.pse.cardit.game.model;

import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.model.player.Player;
import com.pse.cardit.game.model.rules.MaexxleRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaexxleTest {

    final List<APlayer> playerlist =Arrays.asList(new Player(1),new Player(2));

    final Maexxle maexxleGame = new Maexxle(new MaexxleRules(), playerlist);


    @Test
    void validValuePattern() {
        System.out.println(maexxleGame.shakeAndPeak());
    }

    @Test
    void validAndInvalidLieTest() {
        assertFalse(maexxleGame.lie(85), "Is not valid");
        assertTrue(maexxleGame.lie(66), "Is valid");

    }

    @Test
    void checkLie() {
        maexxleGame.checkForLie();
        System.out.println(maexxleGame.getLooser());
    }

}
