package com.pse.cardit.game.service;

import com.pse.cardit.game.model.player.IPlayer;

import java.util.Collection;
import java.util.List;

public interface IGameAdapter {
    String getState();

    GameType getGameType();

    void startGame();

    boolean hasEnded();

    List<IPlayer> getPlayers();


    IPlayer getActivePlayer();

    Collection<IPlayer> getWinners();

    boolean isPlayerActive(IPlayer player);

    String getNextAction();
}
