package com.pse.cardit.game.service.command;

import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.action.IPassAction;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public class PassCommand extends AInGameCommand {

    private static final int NUMBER_OF_PARAMETERS = 0;
    private static final String PREFIX = "pass";
    private final IPassAction adapter;

    public PassCommand(IPassAction adapter) {
        this.adapter = adapter;
    }

    @Override
    public InteractResponse execute(List<String> arguments) {
        if (!adapter.pass()) {
            return new InteractResponse(GameStatus.BAD_INTERACTION, UNSPECIFIC_ERROR);
        }
        return new InteractResponse(GameStatus.OK, "");
    }

    @Override
    public String getRegex() {
        return getPrefix();
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
