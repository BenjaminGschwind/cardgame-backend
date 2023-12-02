package com.pse.cardit.game.service.command;

import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.action.IMakeCallAction;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public class MakeCallCommand extends AInGameCommand {
    private static final int NUMBER_OF_PARAMETERS = 0;
    private static final String PREFIX = "call";
    private final IMakeCallAction adapter;

    public MakeCallCommand(IMakeCallAction adapter) {
        this.adapter = adapter;
    }

    public InteractResponse execute(List<String> arguments) {
        if (adapter.makeCall()) {
            return new InteractResponse(GameStatus.OK, "");
        }
        return new InteractResponse(GameStatus.BAD_INTERACTION, UNSPECIFIC_ERROR);
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
