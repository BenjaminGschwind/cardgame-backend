package com.pse.cardit.lobby.model;

import com.pse.cardit.game.model.player.DifficultyEnum;
import com.pse.cardit.game.service.GameType;

import java.util.List;

public interface IGameLobby extends ILobby {
    void setGameType(GameType gameType);

    GameType getGameType();

    boolean pushBot(Participant participant);

    Participant popBot();

    List<Participant> getBots();

    void setDifficulty(DifficultyEnum difficulty);

    DifficultyEnum getDifficulty();

    void setGameId(String gameId);

    String getGameId();

    boolean changeGameSetting(String rule);

    List<String> getGameSettings();
}
