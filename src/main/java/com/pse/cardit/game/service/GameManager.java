package com.pse.cardit.game.service;

import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.service.executor.AExecutor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class GameManager {
    private static final int MAXIMUM_CONCURRENT_GAMES = 100;
    private static final int GAME_IDENTIFIER_RANGE = 1000000000;
    private static final String TOO_MANY_GAMES_ERROR = "Error, only " + MAXIMUM_CONCURRENT_GAMES + "are supported at "
            + "once. Please try again later.";
    private static GameManager instance;
    private final Map<String, AExecutor> executorsMap;
    private final Random random;
    private int gameIdCounter;

    private GameManager() {
        this.executorsMap = new HashMap<>();
        random = new Random();
        gameIdCounter = 0;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public String createExecutor(GameType gameType, Collection<APlayer> players, List<String> rules) {
        if (MAXIMUM_CONCURRENT_GAMES <= executorsMap.size()) {
            throw new IllegalStateException(TOO_MANY_GAMES_ERROR);
        }
        gameIdCounter = (gameIdCounter + random.nextInt()) % GAME_IDENTIFIER_RANGE;
        String gameId = String.valueOf(gameIdCounter);
        AExecutor executor;
        try {
            executor = gameType.getExecutor(gameId, players, rules);
        }
        catch (IllegalArgumentException e) {
            return "";
        }
        executorsMap.put(gameId, executor);
        return gameId;
    }

    public AExecutor getExecutor(String gameId) {
        return executorsMap.get(gameId);
    }

    public void deleteExecutor(String gameId) {
        executorsMap.remove(gameId);
    }
}
