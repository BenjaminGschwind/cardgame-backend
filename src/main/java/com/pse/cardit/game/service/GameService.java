package com.pse.cardit.game.service;

import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.model.player.Bot;
import com.pse.cardit.game.model.player.DifficultyEnum;
import com.pse.cardit.game.model.player.IPlayer;
import com.pse.cardit.game.model.player.Player;
import com.pse.cardit.game.service.controll.GameChannelResponse;
import com.pse.cardit.game.service.controll.GameStateResponse;
import com.pse.cardit.game.service.controll.InteractResponse;
import com.pse.cardit.game.service.executor.AExecutor;
import com.pse.cardit.leaderboard.service.ILeaderboardService;
import com.pse.cardit.user.model.User;
import com.pse.cardit.user.model.UserType;
import com.pse.cardit.user.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


@Service
public class GameService implements IGameService {
    public static final String DIFFICULTY_KEY = "botDifficulty";
    public static final String GAME_RULES_KEY = "rules";
    private static final DifficultyEnum DEFAULT_DIFFICULTY = DifficultyEnum.MEDIUM;
    private static final int MINIMUM_USERS_FOR_RANKED_GAME = 2;

    private static final String UNKNOWN_GAME_IDENTIFIER_ERROR = "Error, there is no executor with this identifier";
    private static final String DUPLICATE_PLAYER_ERROR = "Error, users can only be active in a single game at a time!";

    private final IUserService userService;
    private final ILeaderboardService leaderboardService;
    private final GameManager gameManager;
    private final Map<Long, IPlayer> playersMap;
    private final Map<IPlayer, String> gameIdMap;

    public GameService(IUserService userService, ILeaderboardService leaderboardService) {
        this.userService = userService;
        this.leaderboardService = leaderboardService;
        this.gameManager = GameManager.getInstance();
        this.playersMap = new HashMap<>();
        this.gameIdMap = new HashMap<>();
    }

    public String getState(String gameId) {
        AExecutor executor = getExecutor(gameId);
        String state = executor.getGameState();
        for (IPlayer p : executor.getPlayers()) {
            state = state.replace("\"username\" : \"" + p.getUserID() + "\"",
                    "\"username\" : \"" + userService.getUser(p.getUserID()).getUsername() + "\"");
            state = state.replace("\"activePlayer\" : \"" + p.getUserID() + "\"",
                    "\"activePlayer\" : \"" + userService.getUser(p.getUserID()).getUsername() + "\"");
        }
        return state;
    }

    public InteractResponse interact(String authToken, String interaction) {
        long userId = userService.getUserFromToken(authToken).getUserId();
        IPlayer player = playersMap.get(userId);
        String id = gameIdMap.get(player);
        AExecutor executor = getExecutor(id);
        Collection<IPlayer> winner = executor.getWinners();
        InteractResponse response = executor.interact(player, interaction);
        if (!winner.equals(executor.getWinners())) {
            this.registerVictory(executor.getIdentifier());
        }
        return response;
    }

    @Override
    public boolean botInteraction(String gameId) {
        AExecutor executor = getExecutor(gameId);
        User user = userService.getUser(executor.getActivePlayer().getUserID());
        if (user.getUserType() != UserType.BOT) {
            return false;
        }
        String botInteraction = executor.getBotInteraction();
        if (botInteraction.isBlank()) {
            return false;
        }
        Collection<IPlayer> winner = executor.getWinners();
        InteractResponse response = executor.interact(executor.getActivePlayer(), botInteraction);
        if (!winner.equals(executor.getWinners())) {
            registerVictory(gameId);
        }
        if (response.getStatus().is2xxSuccessful()) {
            return true;
        }
        Logger.getLogger(this.getClass().getName()).log(new LogRecord(Level.INFO, response.getResponse()));
        throw new AssertionError("Error, Bots should not be presented illegal actions!");
    }

    public String createGame(long[] userIds, GameType gameType, Map<String, String> rules) {
        DifficultyEnum difficulty = DEFAULT_DIFFICULTY;
        if (rules.containsKey(DIFFICULTY_KEY)) {
            try {
                difficulty = DifficultyEnum.valueOf(rules.get(DIFFICULTY_KEY));
            }
            catch (IllegalArgumentException e) {
                throw new AssertionError("The difficulty should be specified using the DifficultyEnum format");
            }
        }
        String rule = rules.get(GAME_RULES_KEY);
        List<String> gameRules = new ArrayList<>();
        if (rule != null) {
            Logger.getGlobal().log(new LogRecord(Level.INFO, rule));
            throw new UnsupportedOperationException("Rule modifications are not implemented.");
        }
        APlayer[] players;
        try {
            players = initialisePlayersAndBots(userIds, difficulty);
        }
        catch (IllegalStateException e) {
            return "";
        }
        String gameIdentifier = gameManager.createExecutor(gameType, Arrays.asList(players), gameRules);
        if (gameIdentifier.isBlank()) {
            return gameIdentifier;
        }
        for (int i = 0; i < userIds.length; i++) {
            if (userService.getUser(userIds[i]).getUserType() != UserType.BOT) {
                playersMap.put(userIds[i], players[i]);
                gameIdMap.put(players[i], gameIdentifier);
            }
        }
        return gameIdentifier;

    }

    /**
     * Will initialise an array of {@link APlayer}s. A new player object will be created for each and inserted in the
     * resulting array in the order they were provided. So that userIdentifiers[i] == APlayers[i].getUserId.
     *
     * @param userIdentifiers the ordered user identifiers
     * @param difficulty      the difficulty setting for all bots
     * @return the full player array
     *
     * @throws IllegalArgumentException when any user id is already part of a managed game
     */
    private APlayer[] initialisePlayersAndBots(long[] userIdentifiers, DifficultyEnum difficulty) {
        APlayer[] players = new APlayer[userIdentifiers.length];
        for (int i = 0; i < userIdentifiers.length; i++) {
            if (userService.getUser(userIdentifiers[i]).getUserType() == UserType.BOT) {
                players[i] = new Bot(userIdentifiers[i], difficulty);
            }
            else {
                players[i] = new Player(userIdentifiers[i]);
                if (playersMap.containsKey(userIdentifiers[i])) {
                    throw new IllegalStateException(DUPLICATE_PLAYER_ERROR);
                }
            }
        }
        return players;
    }

    public void deleteGame(String gameId) {
        if (gameManager.getExecutor(gameId) == null) {
            return;
        }
        AExecutor executor = getExecutor(gameId);
        for (IPlayer player : executor.getPlayers()) {
            playersMap.remove(player.getUserID());
            gameIdMap.remove(player);
        }
        gameManager.deleteExecutor(gameId);
    }

    @Override
    public GameStateResponse getPersonalState(String token) {
        long userId = userService.getUserFromToken(token).getUserId();
        if (!this.playersMap.containsKey(userId)) {
            throw new IllegalArgumentException();
        }
        return new GameStateResponse(getState(gameIdMap.get(playersMap.get(userId))));
    }

    @Override
    public boolean isNotInGame(String authToken, String gameIdentifier) {
        long userId = userService.getUserFromToken(authToken).getUserId();
        IPlayer player = playersMap.get(userId);
        AExecutor executor;
        try {
            executor = getExecutor(gameIdentifier);
        }
        catch (IllegalArgumentException e) {
            return true;
        }
        if (player == null) {
            return true;
        }
        return !executor.getPlayers().contains(player);
    }

    @Override
    public GameChannelResponse getPersonalChannel(String token) {
        long userId = userService.getUserFromToken(token).getUserId();
        IPlayer player = playersMap.get(userId);
        if (player == null) {
            return new GameChannelResponse(Objects.hash(userId), "");
        }
        String gameId = gameIdMap.get(player);
        if (gameId == null) {
            throw new AssertionError("There was a known player but he is in no game. Why is he known then?");
        }
        return new GameChannelResponse(Objects.hash(userId), gameId);
    }

    private void registerVictory(String gameId) {
        AExecutor executor = getExecutor(gameId);
        int amountUsers = 0;
        for (IPlayer player : executor.getPlayers()) {
            if (userService.getUser(player.getUserID()).getUserType() != UserType.BOT) {
                amountUsers++;
            }
        }
        if (amountUsers < MINIMUM_USERS_FOR_RANKED_GAME) {
            return;
        }
        Collection<IPlayer> winners = executor.getWinners();
        for (IPlayer player : executor.getPlayers()) {
            if (userService.getUser(player.getUserID()).getUserType() == UserType.REGISTERED) {
                leaderboardService.addPlayedGameToUser(player.getUserID());
                if (winners.contains(player)) {
                    leaderboardService.addWinToUser(player.getUserID(), executor.getGameType());
                }
            }
        }
    }

    private AExecutor getExecutor(String gameId) {
        if (gameManager.getExecutor(gameId) == null) {
            throw new IllegalArgumentException(UNKNOWN_GAME_IDENTIFIER_ERROR);
        }
        return gameManager.getExecutor(gameId);
    }

    public void resetService() {
        for (String id : new ArrayList<>(gameIdMap.values())) {
            deleteGame(id);
        }
    }
}
