package com.pse.cardit.game.model.player;

/**
 * Bot is a concrete class that extends the APlayer class by adding an additional difficulty attribute.
 * The class represents an Object that should not be used for a real player.
 */
public class Bot extends APlayer {

    private final DifficultyEnum difficulty;

    /**
     * Instantiates a new Bot with the given difficulty and identifier.
     *
     * @param userID     The identifier.
     * @param difficulty The difficulty.
     */
    public Bot(long userID, DifficultyEnum difficulty) {
        super(userID);
        this.difficulty = difficulty;
    }
}
