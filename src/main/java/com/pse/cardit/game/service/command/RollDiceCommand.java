package com.pse.cardit.game.service.command;

import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.action.IRollDiceAction;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public class RollDiceCommand extends AInGameCommand {
    private static final int NUMBER_OF_PARAMETERS = 0;
    private static final String PREFIX = "rollDice";
    private final IRollDiceAction adapter;

    public RollDiceCommand(IRollDiceAction adapter) {
        this.adapter = adapter;
    }

    public InteractResponse execute(List<String> arguments) {
        return new InteractResponse(GameStatus.CREATED, adapter.rollDice());
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
