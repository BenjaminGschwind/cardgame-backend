package com.pse.cardit.game.model.botlogic;

import com.pse.cardit.game.model.AGame;
import com.pse.cardit.game.model.Maexxle;
import com.pse.cardit.game.model.player.Bot;
import com.pse.cardit.game.model.rules.MaexxleRules;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * MaexxleBotLogic is a concrete class that implements the IBotLogic interface and provides a static method
 * that calculates the next move a Bot should do in an Maexxle game.
 */
public final class MaexxleBotLogic implements IBotLogic {

    private static final Random RANDOM = new Random();

    private static final String SHAKE_MOVE = "rollDice";
    private static final String LIE_MOVE = "put ";
    private static final String CHECK_LIE_MOVE = "call";

    private static final List<Integer> HIGH_ROLLS = Arrays.asList(21, 11, 22, 33, 44, 55, 66);

    private MaexxleBotLogic() {

    }

    /**
     * Calculates the next Maexxle move the given bot should do and returns it as a String.
     *
     * @param game The game.
     * @param bot  The bot.
     * @return The String that contains the move.
     */
    public static String evaluateMove(AGame game, Bot bot) {
        Maexxle maexxleGame = (Maexxle) game;
        MaexxleRules rules = maexxleGame.getMaexxleRules();

        if ((!maexxleGame.hasShaken() && maexxleGame.hasLied()) && HIGH_ROLLS.contains(rules.getPrevious())
                && RANDOM.nextInt(101) > 80) {
            return CHECK_LIE_MOVE;
        }
        else if (!maexxleGame.hasShaken()) {
            return SHAKE_MOVE;
        }
        else if (maexxleGame.hasShaken() && maexxleGame.hasLied()) {
            int toLie = switch (rules.getPrevious()) {
                // Die hier genutzen Magicnumbers stellen die in Mäxxle möglichen würfelbaren Augenwerte dar
                // und die daraus folgenden nächst besseren Werte.
                case 11, 22, 33, 44, 55 -> rules.getPrevious() + 11;
                case 66 -> 21;
                case 31, 41, 42, 51, 52, 53, 61, 62, 63, 64 -> rules.getPrevious() + 1;
                case 32 -> 41;
                case 43 -> 51;
                case 54 -> 61;
                case 65 -> 11;
                default -> 21;
            };
            return LIE_MOVE + toLie;
        }

        return null;
    }


}
