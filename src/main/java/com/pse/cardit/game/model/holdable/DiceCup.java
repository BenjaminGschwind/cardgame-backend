package com.pse.cardit.game.model.holdable;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

/**
 * DiceCup is a concrete class that provides the representation of a cup containing a collection of dices
 * and given interactions with that cup
 */
public class DiceCup {
    private final List<Dice> dices;


    /**
     * Instantiates a new DiceCup.
     */
    public DiceCup() {
        this.dices = new ArrayList<>();
    }

    /**
     * Simulates shaking of a DiceCup
     */
    public void shakeCup() {
        for (Dice d : dices) {
            d.rollDice();
        }
    }

    /**
     * Returns the top facing values from every dice.
     *
     * @return Top facing values with format x:x:x...
     */
    public String getDiceValues() {
        ArrayList<Integer> tops = new ArrayList<>();
        for (Dice d : dices) {
            tops.add(d.getTop());
        }
        return Joiner.on(":").join(tops);
    }

    /**
     * Removes the dice with the top facing given value.
     *
     * @param value The value of the dice that should be removed.
     * @return The dice.
     */
    public Dice removeDice(int value) {
        for (Dice d : dices) {
            if (d.getTop() == value) {
                return dices.remove(dices.indexOf(d));
            }
        }
        return null;
    }

    /**
     * Places a given Dice in the DiceCup.
     *
     * @param dice The dice to be placed back.
     * @return true if success.
     */
    public boolean returnDice(Dice dice) {
        return dices.add(dice);
    }
}
