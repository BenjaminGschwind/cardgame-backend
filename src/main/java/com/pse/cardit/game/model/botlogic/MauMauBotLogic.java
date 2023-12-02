package com.pse.cardit.game.model.botlogic;

import com.pse.cardit.game.model.AGame;
import com.pse.cardit.game.model.MauMau;
import com.pse.cardit.game.model.holdable.Card;
import com.pse.cardit.game.model.holdable.ColorEnum;
import com.pse.cardit.game.model.holdable.ValueEnum;
import com.pse.cardit.game.model.player.Bot;
import com.pse.cardit.game.model.rules.MauMauRules;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * MauMauBotLogic is a concrete class that implements the IBotLogic interface and provides a static method
 * that calculates the next move a Bot should do in an MauMau game.
 */
public final class MauMauBotLogic implements IBotLogic {

    private static final String DISCARD_MOVE = "discard ";
    private static final String PLAY_MOVE = "play ";
    private static final String DRAW_MOVE = "drawFromDrawpile 1";
    private static final String PASS_MOVE = "pass";

    private static final Random RANDOM = new Random();

    private MauMauBotLogic() {
    }

    /**
     * Calculates the next MauMau move the given bot should do and returns it as a String.
     *
     * @param game The game.
     * @param bot  The bot.
     * @return The String that contains the move.
     */
    public static String evaluateMove(AGame game, Bot bot) {
        MauMau mauMauGame = (MauMau) game;
        MauMauRules rules = mauMauGame.getMauMauRule();

        ValueEnum topValue = rules.getValidNumber();

        //Super evil Typecasting hack da wir wissen, das im Inventar nur Karten sind.
        List<Card> cards = (List<Card>) (List<?>) bot.getInventory();

        if (mauMauGame.getDrawDeck().getDeckSize() == 0) {
            return PASS_MOVE;
        }

        if (topValue == ValueEnum.SEVEN && !mauMauGame.isHasDrawn()) {
            for (Card c : cards) {
                if (c.getValue() == ValueEnum.SEVEN) {
                    return DISCARD_MOVE + c;
                }
            }
            return DRAW_MOVE;
        }
        for (Card c : cards) {
            if (rules.checkPut(c, Optional.empty())) {
                if (c.getValue() == ValueEnum.JACK) {
                    return PLAY_MOVE + c + " "
                            + ColorEnum.values()[RANDOM.nextInt(ColorEnum.values().length)].getCodec();
                }
                else {
                    return DISCARD_MOVE + c;
                }
            }
        }
        if (mauMauGame.isHasDrawn()) {
            return PASS_MOVE;
        }
        return DRAW_MOVE;
    }
}
