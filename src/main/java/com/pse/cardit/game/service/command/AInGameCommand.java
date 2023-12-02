package com.pse.cardit.game.service.command;

import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public abstract class AInGameCommand implements ICommand {
    public static final String KEYWORD_ARGS_SEPARATOR = " ";

    protected static final String UNSPECIFIC_ERROR = "Error - Probably you are not able to do that right now? "
            + "Please tell me when reading this!";
    private static final String ILLEGAL_ARGUMENTS_AMOUNT = "Error - Mismatched amount of arguments! Expected %d but "
            + "got %d.";

    public InteractResponse apply(List<String> arguments) {
        if (arguments.size() != this.getNumberOfParameters()) {
            return new InteractResponse(GameStatus.BAD_REQUEST, String.format(ILLEGAL_ARGUMENTS_AMOUNT,
                    this.getNumberOfParameters(), arguments.size()));
        }
        return this.execute(arguments);
    }

    public abstract InteractResponse execute(List<String> arguments);
}
