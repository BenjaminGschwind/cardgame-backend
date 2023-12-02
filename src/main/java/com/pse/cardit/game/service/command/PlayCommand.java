package com.pse.cardit.game.service.command;

import com.pse.cardit.game.model.holdable.Card;
import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.action.IPlayAction;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public class PlayCommand extends AInGameCommand {
    private static final int NUMBER_OF_PARAMETERS = 2;
    private static final String PREFIX = "play";
    private final IPlayAction adapter;

    public PlayCommand(IPlayAction adapter) {
        this.adapter = adapter;
    }

    public InteractResponse execute(List<String> arguments) {
        if (adapter.discard(List.of(arguments.get(0)), arguments.get(1))) {
            return new InteractResponse(GameStatus.OK, "");
        }
        return new InteractResponse(GameStatus.BAD_INTERACTION, UNSPECIFIC_ERROR);
    }

    @Override
    public String getRegex() {
        return PREFIX + KEYWORD_ARGS_SEPARATOR + "(" + Card.REGEX_FORMAT + ";)*" + Card.REGEX_FORMAT + " [H,S,C,D]";
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
