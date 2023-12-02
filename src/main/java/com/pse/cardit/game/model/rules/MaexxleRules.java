package com.pse.cardit.game.model.rules;

/**
 * MaexxleRules is a concrete class that provides an environment to check and validate moves
 * and actions in a Maexxle game.
 */
public class MaexxleRules implements IRules {

    private int previous;

    /**
     * Instantiates a new MaexxleRules object.
     */
    public MaexxleRules() {
        this.previous = 0;
    }


    /**
     * Validates if a given value is a propper value a player could lie in a Maexxle game.
     *
     * @param toValidate The value to vaidate.
     * @return True if valid.
     */
    public boolean validateLie(int toValidate) {
        return switch (toValidate) {
            case 11, 22, 33, 44, 55, 66 -> true; //Päsche
            case 21 -> true; //Mäxxle
            case 31, 32 -> true; //30er
            case 41, 42, 43 -> true; //40er
            case 51, 52, 53, 54 -> true; //50er
            case 61, 62, 63, 64, 65 -> true; //60er
            default -> false;
        };
    }


    /**
     * Gets previous.
     *
     * @return the previous
     */
    public int getPrevious() {
        return previous;
    }

    /**
     * Sets previous.
     *
     * @param previous the previous
     */
    public void setPrevious(int previous) {
        this.previous = previous;
    }
}
