package com.pse.cardit.game.model.holdable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * Dice is concrete class implementing the IHoldable interface that provides
 * the representation of a dice consisting of n faces with n values.
 */
public class Dice implements IHoldable {

    private final int[] diceFaces;
    private int top;

    private final Random rand;

    /**
     * Instantiates a new Dice with n faces
     *
     * @param faces The number of faces i.e. values the dice has
     */
    public Dice(int faces) {
        int[] dice = new int[faces];
        for (int i = 0; i < faces; i++) {
            dice[i] = i + 1;
        }
        this.diceFaces = dice;
        this.top = dice[0];
        this.rand = new Random();
    }

    /**
     * Get dice.
     *
     * @return The dice.
     */
    public int[] getDiceFaces() {
        return diceFaces;
    }

    /**
     * Gets top.
     *
     * @return The top.
     */
    public int getTop() {
        return top;
    }

    /**
     * Simulates the rolling of the dice.
     */
    public void rollDice() {
        this.top = diceFaces[rand.nextInt(diceFaces.length)];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dice dice = (Dice) o;
        return top == dice.top && Arrays.equals(diceFaces, dice.diceFaces) && Objects.equals(rand, dice.rand);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(top);
        result = 31 * result + Arrays.hashCode(diceFaces);
        return result;
    }
}
