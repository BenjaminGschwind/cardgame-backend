package com.pse.cardit.game.service.command;

import com.pse.cardit.game.model.holdable.Card;
import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.action.IDiscardAction;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public class DiscardCommand extends AInGameCommand {

    private static final int NUMBER_OF_PARAMETERS = 1;
    private static final String PREFIX = "discard";
    private final IDiscardAction adapter;

    public DiscardCommand(IDiscardAction adapter) {
        this.adapter = adapter;
    }

    public InteractResponse execute(List<String> arguments) {
        if (adapter.discard(arguments)) {
            return new InteractResponse(GameStatus.OK, "");
        }
        return new InteractResponse(GameStatus.BAD_INTERACTION, UNSPECIFIC_ERROR + "I guess you did not draw even "
                + "though you must because there is a seven on top right now.");
    }

    @Override
    public String getRegex() {
        return PREFIX + KEYWORD_ARGS_SEPARATOR + "(" + Card.REGEX_FORMAT + ";)*" + Card.REGEX_FORMAT;
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public int getNumberOfParameters() {
        return NUMBER_OF_PARAMETERS;
    }
}
