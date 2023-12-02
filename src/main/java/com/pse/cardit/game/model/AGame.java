package com.pse.cardit.game.model;

import com.pse.cardit.game.model.player.APlayer;

import java.util.List;

/**
 * AGame is an abstract class that provides the basic attributes and methods every class
 * that inherits by AGame needs to have.
 */
public abstract class AGame {

    private static final String ILLEGAL_AMOUNT_OF_PLAYERS = "Error, to create this game there need to be between %d "
            + "and %d players. You tried to play with %d.";

    /**
     * The Round counter.
     */
    protected int roundCounter;
    /**
     * The Game finished.
     */
    protected boolean gameFinished;
    private final List<APlayer> playerList;
    private APlayer currentPlayer;

    /**
     * Instantiates a new AGame with the given collection of APlayer.
     *
     * @param playerList The list of players that are participation in the AGame.
     */
    protected AGame(List<APlayer> playerList) {
        if (getMinPlayer() > playerList.size() || getMaxPlayer() < playerList.size()) {
            throw new IllegalArgumentException(String.format(ILLEGAL_AMOUNT_OF_PLAYERS, getMinPlayer(),
                    getMaxPlayer(), playerList.size()));
        }
        this.playerList = playerList;
        this.currentPlayer = playerList.get(0);
        this.roundCounter = 0;
        this.gameFinished = false;
    }

    /**
     * Select the next player in the player list.
     *
     * @return True if success.
     */
    public boolean selectNextPlayer() {
        int next = playerList.indexOf(currentPlayer) + 1;
        currentPlayer = playerList.get(next % playerList.size());
        return true;
    }


    /**
     * HasEnded boolean.
     *
     * @return the boolean
     */
    public boolean hasEnded() {
        return gameFinished;
    }


    //Default setter & getter


    /**
     * Gets playerList.
     *
     * @return The playerList.
     */
    public List<APlayer> getPlayerList() {
        return playerList;
    }

    /**
     * Gets current player.
     *
     * @return the current player
     */
    public APlayer getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets round counter.
     *
     * @return the round counter
     */
    public int getRoundCounter() {
        return roundCounter;
    }

    /**
     * Gets min player.
     *
     * @return the min player
     */
    public abstract int getMinPlayer();

    /**
     * Gets max player.
     *
     * @return the max player
     */
    public abstract int getMaxPlayer();

    /**
     * Sets current player.
     *
     * @param currentPlayer the current player
     */
    public void setCurrentPlayer(APlayer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
