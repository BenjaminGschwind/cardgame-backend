package com.pse.cardit.game.service.executor;

import com.pse.cardit.game.model.AGame;
import com.pse.cardit.game.model.player.IPlayer;
import com.pse.cardit.game.service.IGameAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class AGameAdapter implements IGameAdapter {

    protected abstract AGame getGame();

    @Override
    public void startGame() {
    }

    @Override
    public boolean hasEnded() {
        return getGame().hasEnded();
    }

    @Override
    public List<IPlayer> getPlayers() {
        return new ArrayList<>(getGame().getPlayerList());
    }

    @Override
    public IPlayer getActivePlayer() {
        return getGame().getCurrentPlayer();
    }

    @Override
    public boolean isPlayerActive(IPlayer player) {
        if (getGame().hasEnded()) {
            return false;
        }
        return player.equals(getActivePlayer());
    }
}
