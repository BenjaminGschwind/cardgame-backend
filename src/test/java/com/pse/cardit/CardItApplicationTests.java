package com.pse.cardit;

import com.pse.cardit.game.model.holdable.Dice;
import com.pse.cardit.game.model.holdable.DiceCup;
import org.junit.jupiter.api.Test;

class CardItApplicationTests {

    @Test
    void diceCupTest() {
        Dice dice1 = new Dice(6);
        Dice dice2 = new Dice(6);
        Dice dice3 = new Dice(6);
        DiceCup diceCup = new DiceCup();
        diceCup.returnDice(dice1);
        diceCup.returnDice(dice2);
        diceCup.returnDice(dice3);
        System.out.println(diceCup.getDiceValues());
        diceCup.shakeCup();
        System.out.println(diceCup.getDiceValues());
    }

}
