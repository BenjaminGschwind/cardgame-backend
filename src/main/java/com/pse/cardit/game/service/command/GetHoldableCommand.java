package com.pse.cardit.game.service.command;

import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.action.IGetHoldableAction;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public class GetHoldableCommand extends AInGameCommand {

    private static final int NUMBER_OF_PARAMETERS = 0;
    private static final String PREFIX = "getHand";
    private final IGetHoldableAction adapter;

    public GetHoldableCommand(IGetHoldableAction adapter) {
        this.adapter = adapter;
    }

    public InteractResponse execute(List<String> arguments) {

        return new InteractResponse(GameStatus.CREATED, adapter.getHoldable());
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
