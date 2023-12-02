package com.pse.cardit.game.service.command;

import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.action.IPutValueAction;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public class PutValueCommand extends AInGameCommand {

    private static final int NUMBER_OF_PARAMETERS = 1;
    private static final String PREFIX = "put";
    private final IPutValueAction adapter;

    public PutValueCommand(IPutValueAction adapter) {
        this.adapter = adapter;
    }

    public InteractResponse execute(List<String> arguments) {
        int value;
        try {
            value = Integer.parseInt(arguments.get(0));
        }
        catch (NumberFormatException e) {
            return new InteractResponse(GameStatus.BAD_REQUEST, "Error - Expected to get an integer, but "
                    + arguments.get(0) + " is not!");
        }
        if (adapter.putValue(value)) {
            return new InteractResponse(GameStatus.OK, "");
        }
        return new InteractResponse(GameStatus.BAD_INTERACTION, UNSPECIFIC_ERROR);
    }

    @Override
    public String getRegex() {
        return PREFIX + KEYWORD_ARGS_SEPARATOR + "[1-9][0-9]*";
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
