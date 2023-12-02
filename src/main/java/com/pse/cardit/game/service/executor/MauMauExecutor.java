package com.pse.cardit.game.service.executor;

import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.model.player.IPlayer;
import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.IGameAdapter;
import com.pse.cardit.game.service.command.DiscardCommand;
import com.pse.cardit.game.service.command.DrawFromDrawPileCommand;
import com.pse.cardit.game.service.command.GetHoldableCommand;
import com.pse.cardit.game.service.command.ICommand;
import com.pse.cardit.game.service.command.PassCommand;
import com.pse.cardit.game.service.command.PlayCommand;
import com.pse.cardit.game.service.controll.InteractResponse;
import com.pse.cardit.game.service.exceptions.UnsupportedCommandException;

import java.util.Collection;
import java.util.List;

public class MauMauExecutor extends AExecutor {
    private final MauMauAdapter gameAdapter;

    public MauMauExecutor(String identifier, Collection<APlayer> players, List<String> rules) {
        super(identifier);
        this.gameAdapter = new MauMauAdapter(players, rules);
        this.addCommands(List.of(
                new DrawFromDrawPileCommand(gameAdapter),
                new DiscardCommand(gameAdapter),
                new GetHoldableCommand(gameAdapter),
                new PlayCommand(gameAdapter),
                new PassCommand(gameAdapter)
        ));

    }

    @Override
    public InteractResponse interact(IPlayer player, String interaction) {
        ICommand command;
        try {
            command = getMatchingCommand(interaction);
        }
        catch (UnsupportedCommandException e) {
            return new InteractResponse(GameStatus.BAD_REQUEST, e.getMessage());
        }
        if (command.getClass() == GetHoldableCommand.class) {
            return new InteractResponse(GameStatus.CREATED, gameAdapter.getHoldable(player));
        }
        InteractResponse response = super.interact(player, interaction);
        if (response.getStatus().is2xxSuccessful()) {
            return interact(player, "getHand");
        }
        return response;
    }

    @Override
    protected IGameAdapter getAdapter() {
        return gameAdapter;
    }
}
