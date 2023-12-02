package com.pse.cardit.game.service.command;

import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.action.IDrawFromDrawPileAction;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public class DrawFromDrawPileCommand extends AInGameCommand {

    private static final int NUMBER_OF_PARAMETERS = 1;
    private static final String PREFIX = "drawFromDrawpile";
    private final IDrawFromDrawPileAction adapter;

    public DrawFromDrawPileCommand(IDrawFromDrawPileAction adapter) {
        this.adapter = adapter;
    }

    public InteractResponse execute(List<String> arguments) {
        int amount;
        try {
            amount = Integer.parseInt(arguments.get(0));
        }
        catch (NumberFormatException e) {
            return new InteractResponse(GameStatus.BAD_REQUEST, "Error - Expected to get an integer, but "
                    + arguments.get(0) + " is not!");
        }
        String result = adapter.drawFromDraw(amount);
        if (result.isBlank()) {
            return new InteractResponse(GameStatus.BAD_INTERACTION, UNSPECIFIC_ERROR + "I suppose you have already "
                    + "drawn before. ");
        }
        return new InteractResponse(GameStatus.CREATED, result);
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
