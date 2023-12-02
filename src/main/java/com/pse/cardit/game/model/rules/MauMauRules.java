package com.pse.cardit.game.model.rules;

import com.pse.cardit.game.model.holdable.Card;
import com.pse.cardit.game.model.holdable.ColorEnum;
import com.pse.cardit.game.model.holdable.ValueEnum;

import java.util.Optional;

/**
 * MauMauRules is a concrete class that provides an environment to check and validate moves
 * and actions in a MauMau game.
 */
public class MauMauRules implements IRules {

    private ColorEnum validColor;
    private ValueEnum validNumber;

    /**
     * Instantiates a new MauMauRules object.
     */
    public MauMauRules() {
        this.validColor = null;
        this.validNumber = null;
    }

    /**
     * Checks if a player has already drawn a card.
     *
     * @param hasDrawn The boolean.
     * @return True if already drawn.
     */
    public boolean checkDraw(boolean hasDrawn) {
        return hasDrawn;
    }

    /**
     * Checks if the card the player wants to place is possible to place and if a given color is a valid color.
     *
     * @param card  The card to place.
     * @param color The color to check.
     * @return True if valid.
     */
    public boolean checkPut(Card card, Optional<ColorEnum> color) {
        return (card.getValue() == ValueEnum.JACK && color.isPresent())
                || (card.getValue() == validNumber) || (card.getColor() == validColor);

    }

    /**
     * Gets valid color.
     *
     * @return the valid color
     */
    public ColorEnum getValidColor() {
        return validColor;
    }

    /**
     * Sets valid color.
     *
     * @param validColor the valid color
     */
    public void setValidColor(ColorEnum validColor) {
        this.validColor = validColor;
    }

    /**
     * Gets valid number.
     *
     * @return the valid number
     */
    public ValueEnum getValidNumber() {
        return validNumber;
    }

    /**
     * Sets valid number.
     *
     * @param validNumber the valid number
     */
    public void setValidNumber(ValueEnum validNumber) {
        this.validNumber = validNumber;
    }
}
