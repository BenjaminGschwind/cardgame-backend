package com.pse.cardit.game.service.executor;

import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.service.IGameAdapter;
import com.pse.cardit.game.service.command.MakeCallCommand;
import com.pse.cardit.game.service.command.PutValueCommand;
import com.pse.cardit.game.service.command.RollDiceCommand;

import java.util.Collection;
import java.util.List;

public class MiaExecutor extends AExecutor {

    private final MiaAdapter gameAdapter;

    public MiaExecutor(String identifier, Collection<APlayer> players, List<String> rules) {
        super(identifier);
        this.gameAdapter = new MiaAdapter(players, rules);
        this.addCommands(List.of(
                new RollDiceCommand(gameAdapter),
                new PutValueCommand(gameAdapter),
                new MakeCallCommand(gameAdapter)
        ));

    }

    @Override
    protected IGameAdapter getAdapter() {
        return gameAdapter;
    }
}
