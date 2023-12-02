package com.pse.cardit.game.service.command;

import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.List;

public interface ICommand {
    InteractResponse apply(List<String> arguments);

    String getRegex();

    String getPrefix();

    int getNumberOfParameters();
}
