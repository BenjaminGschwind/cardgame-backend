package com.pse.cardit.game.model.botlogic;

import com.pse.cardit.game.model.AGame;
import com.pse.cardit.game.model.player.Bot;

/**
 * IBotLogic is an interface that provides a methode to calculate the next move that a bot should play in the given
 * game.
 */
public interface IBotLogic {

    /**
     * Calculates the next move the given bot should do in the given game and returns it as a String.
     *
     * @param game The game.
     * @param bot  The bot.
     * @return The String that contains the move.
     */
    static String evaluateMove(AGame game, Bot bot) {
        return null;
    }


}
