package com.pse.cardit.game.model;

import com.pse.cardit.game.model.holdable.Dice;
import com.pse.cardit.game.model.holdable.DiceCup;
import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.model.rules.MaexxleRules;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Maexxle is a concrete class that extends the AGame.
 * It contains methodes and attributes needed to play a Maexxle game.
 */
public class Maexxle extends AGame {

    private static final int MINIMUM_AMOUNT_PLAYERS = 2;
    private static final int MAXIMUM_AMOUNT_PLAYERS = 8;
    private final DiceCup diceCup;
    private final MaexxleRules maexxleRules;
    private int lastLie;
    private int lastRoll;
    private boolean hasShaken;
    private boolean hasLied;

    private APlayer looser;

    private boolean playable;

    /**
     * Instantiates a new Maexxle game with the given players and rule.
     *
     * @param rule       The rule.
     * @param playerList The list of APlayer participating.
     */
    public Maexxle(MaexxleRules rule, List<APlayer> playerList) {
        super(playerList);
        this.diceCup = new DiceCup();
        this.maexxleRules = rule;
        this.diceCup.returnDice(new Dice(6));
        this.diceCup.returnDice(new Dice(6));
        initRound();
    }

    /**
     * Shakes the dice-cup and returns the value of the dices.
     *
     * @return The value.
     */
    public int shakeAndPeak() {
        if (hasShaken) {
            throw new IllegalStateException("Player has already shaken the cup");
        }
        maexxleRules.setPrevious(lastLie);
        diceCup.shakeCup();
        int value = sortAndConvertDiceCup(diceCup.getDiceValues());
        if (maexxleRules.getPrevious() > value)  {
            maexxleRules.setPrevious(value);
        }
        this.lastRoll = value;
        this.hasShaken = true;
        return value;
    }


    /**
     * Lie with the given value.
     *
     * @param toLie Value to lie.
     * @return True if lie wa a success.
     */
    public boolean lie(int toLie) {
        if (maexxleRules.validateLie(toLie) && !hasLied) {
            hasLied = true;
            lastLie = toLie;
            return true;
        }
        return false;
    }

    /**
     * Check if the said value was a lie.
     *
     * @return True if it was a lie.
     */
    public boolean checkForLie() {
        if (lastLie > maexxleRules.getPrevious()) {
            int prevIndex = getPlayerList().indexOf(getCurrentPlayer()) - 1;
            looser = getPlayerList().get(Math.floorMod(prevIndex, getPlayerList().size()));
            checkForLooser();
            return true;
        }
        else {
            looser = getCurrentPlayer();
            checkForLooser();
            return false;
        }

    }

    private void checkForLooser() {
        if ((looser != null) || (maexxleRules.getPrevious() == 21)) {
            playable = false;
        }
    }

    private void initRound() {
        this.hasShaken = false;
        this.hasLied = false;
        this.lastLie = 0;
        this.playable = true;
        this.looser = null;
        maexxleRules.setPrevious(0);
    }

    @Override
    public boolean selectNextPlayer() {
        if (hasShaken || hasLied) {

            hasShaken = false;
            hasLied = false;
            return super.selectNextPlayer();
        }
        return false;
    }

    @Override
    public int getMinPlayer() {
        return MINIMUM_AMOUNT_PLAYERS;
    }

    @Override
    public int getMaxPlayer() {
        return MAXIMUM_AMOUNT_PLAYERS;
    }

    /**
     * Next round.
     */
    public void nextRound() {
        initRound();
        roundCounter++;
    }


    private int sortAndConvertDiceCup(String topValues) {
        List<Integer> values = new java.util.ArrayList<>(
            Stream.of(topValues.split(":")).map(Integer::parseInt).toList());
        values.sort(Collections.reverseOrder());
        int out = 0;
        // assuming list is of type List<Integer>
        for (Integer i : values) {
            out = 10 * out + i;
        }
        return out;
    }


    /**
     * Has shaken boolean.
     *
     * @return the boolean
     */
    public boolean hasShaken() {
        return hasShaken;
    }

    /**
     * Has lied boolean.
     *
     * @return the boolean
     */
    public boolean hasLied() {
        return !hasLied;
    }

    /**
     * Gets looser.
     *
     * @return the looser
     */
    public APlayer getLooser() {
        return looser;
    }

    /**
     * Is playable boolean.
     *
     * @return the boolean
     */
    public boolean isPlayable() {
        return playable;
    }

    /**
     * Gets maexxle rules.
     *
     * @return the maexxle rules
     */
    public MaexxleRules getMaexxleRules() {
        return maexxleRules;
    }

    /**
     * Gets last lie.
     *
     * @return the last lie
     */
    public int getLastLie() {
        return lastLie;
    }

    /**
     * Gets last roll.
     *
     * @return the last roll
     */
    public int getLastRoll() {
        return lastRoll;
    }
}
