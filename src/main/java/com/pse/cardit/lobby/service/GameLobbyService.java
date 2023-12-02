package com.pse.cardit.lobby.service;

import com.pse.cardit.game.model.player.DifficultyEnum;
import com.pse.cardit.game.service.GameType;
import com.pse.cardit.game.service.IGameService;
import com.pse.cardit.lobby.exceptions.UndesiredStateException;
import com.pse.cardit.lobby.model.IGameLobby;
import com.pse.cardit.lobby.model.Participant;
import com.pse.cardit.lobby.model.Rank;
import com.pse.cardit.lobby.model.ReadyState;
import com.pse.cardit.lobby.model.Visibility;
import com.pse.cardit.lobby.service.controll.ChangeSettingsRequest;
import com.pse.cardit.lobby.service.controll.LobbyStateResponse;
import com.pse.cardit.lobby.service.controll.LobbyStomp;
import com.pse.cardit.security.config.JwtService;
import com.pse.cardit.user.model.User;
import com.pse.cardit.user.service.IUserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.pse.cardit.game.service.GameService.DIFFICULTY_KEY;
import static com.pse.cardit.game.service.GameService.GAME_RULES_KEY;
import static com.pse.cardit.lobby.service.GameLobbyManager.UNKNOWN_LOBBY_CODE_ERROR;

public class GameLobbyService implements IGameLobbyService {
    private static final String KNOWN_USER_NO_LOBBY_ERROR = "Error, if a participant is known to the service, he "
            + "should also be in some lobby.";
    //TODO: should be 2
    private static final int MINIMUM_AMOUNT_USER_PLAYERS = 1;

    private final Map<Long, Participant> participantsMap;
    private final IUserService userService;
    private final IGameService gameService;
    private final JwtService jwtService;
    private final GameLobbyManager lobbyManager;

    public GameLobbyService(IUserService userService, IGameService gameService, JwtService jwtService) {
        this.userService = userService;
        this.gameService = gameService;
        this.jwtService = jwtService;
        this.participantsMap = new HashMap<>();
        this.lobbyManager = GameLobbyManager.getInstance();
    }

    @Override
    public String createLobby(String token, GameType gameType) {
        if (gameType == null) {
            return "";
        }
        Participant participant = createParticipant(token);
        IGameLobby lobby = lobbyManager.getLobby(participant);
        if (lobby != null) {
            removeFromLobby(token);
        }
        participant = createParticipant(token);
        String lobbyCode = lobbyManager.createLobby(participant, gameType);
        if (lobbyCode.isBlank()) {
            if (lobby == null) {
                clear(participant);
            }
            else {
                addToLobby(token, lobby.getLobbyCode().toString());
            }
        }
        return lobbyCode;
    }

    @Override
    public String startGame(String authToken) {
        long userId = userService.getUserFromToken(authToken).getUserId();
        Participant participant = getParticipant(userId);
        if (participant.getRank().compareTo(Rank.CAN_START_GAME_RANK) < 0) {
            return "";
        }
        IGameLobby lobby = this.getLobby(participant);
        List<Participant> participants = lobby.getParticipants();
        if (participants.size() < MINIMUM_AMOUNT_USER_PLAYERS) {
            return "";
        }
        participants.addAll(lobby.getBots());
        long[] userIdentifiers = new long[participants.size()];
        for (int i = 0; i < participants.size(); i++) {
            Participant p = participants.get(i);
            if (!p.getReadyState().isCanStartGame()) {
                return "";
            }
            userIdentifiers[i] = p.getUser().getUserId();
        }
        Map<String, String> rules = new HashMap<>();
        List<String> gameRules = lobby.getGameSettings();
        if (!gameRules.isEmpty()) {
            rules.put(GAME_RULES_KEY, gameRules.toString());
        }
        rules.put(DIFFICULTY_KEY, lobby.getDifficulty().toString());
        deleteGame(lobby);
        String gameId = gameService.createGame(userIdentifiers, lobby.getGameType(), rules);
        lobby.setActive(!gameId.isBlank());
        lobby.setGameId(gameId);
        return gameId;
    }

    @Override
    public boolean modifySettings(String authToken, ChangeSettingsRequest changeSettingsRequest) {
        if (changeSettingsRequest.getVisibility() == null || changeSettingsRequest.getGameType() == null
                || changeSettingsRequest.getDifficulty() == null || changeSettingsRequest.getRules() == null) {
            return false;
        }
        long userId = userService.getUserFromToken(authToken).getUserId();
        Participant participant = getParticipant(userId);
        if (participant.getRank().compareTo(Rank.CAN_CHANGE_SETTINGS) < 0) {
            return false;
        }
        IGameLobby lobby = this.getLobby(participant);
        lobby.setVisibility(Visibility.valueOf(formatEnumValue(changeSettingsRequest.getVisibility())));
        lobby.setGameType(GameType.valueOf(formatEnumValue(changeSettingsRequest.getGameType())));
        lobby.setAfkTimer(changeSettingsRequest.getAfkTimer());
        if (lobby.getBots().size() > changeSettingsRequest.getAmountBots()) {
            for (int i = lobby.getBots().size(); i > changeSettingsRequest.getAmountBots() && i > 0; i--) {
                removeBot(lobby);
            }
        }
        else if (lobby.getBots().size() < changeSettingsRequest.getAmountBots()) {
            for (int i = lobby.getBots().size(); i < changeSettingsRequest.getAmountBots(); i++) {
                //TODO give them names!
                User user = userService.addBot("BOT " + i);
                if (!lobby.pushBot(new Participant(user))) {
                    userService.deleteUser(user);
                    break;
                }
            }
        }

        lobby.setDifficulty(DifficultyEnum.valueOf(formatEnumValue(changeSettingsRequest.getDifficulty())));
        for (String rule : changeSettingsRequest.getRules()) {
            lobby.changeGameSetting(rule);
        }
        return true;
    }

    @Override
    public LobbyStateResponse getLobbyState(String lobbyCode) {
        IGameLobby lobby = lobbyManager.getLobby(lobbyCode);
        if (lobby == null) {
            throw new IllegalArgumentException(UNKNOWN_LOBBY_CODE_ERROR);
        }
        return new LobbyStateResponse(lobby);
    }

    @Override
    public LobbyStateResponse getPersonalLobbyState(String token) {
        long userId = jwtService.extractUserId(token);
        if (!this.participantsMap.containsKey(userId)) {
            throw new IllegalArgumentException();
        }
        IGameLobby lobby = this.getLobby(this.participantsMap.get(userId));
        return getLobbyState(lobby.getLobbyCode().toString());
    }

    @Override
    public boolean addToLobby(String token, String lobbyCode) {
        IGameLobby lobby = lobbyManager.getLobby(lobbyCode);
        if (lobby == null) {
            return false;
        }
        if (lobby.getParticipantLimit() <= lobby.getParticipants().size()) {
            return false;
        }
        Participant participant = createParticipant(token);
        IGameLobby currentLobby = lobbyManager.getLobby(participant);
        if (currentLobby != null) {
            if (currentLobby.getLobbyCode().toString().equals(lobbyCode)) {
                return false;
            }
            removeFromLobby(token);
        }
        participant = createParticipant(token);
        return lobby.add(participant);
    }

    @Override
    public boolean removeFromLobby(String authToken, String targetName) {
        Participant host = getParticipant(userService.getUserFromToken(authToken).getUserId());
        Participant target = getParticipant(userService.getUser(targetName).getUserId());
        if (host == null || target == null) {
            return false;
        }
        if (hasNoPermissions(host, target, Rank.CAN_KICK_OTHERS)) {
            return false;
        }
        IGameLobby lobby = this.getLobby(host);
        if (lobby.remove(target)) {
            deleteGame(lobby);
            clear(target);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeFromLobby(String token) {
        Participant participant = getParticipant(token);
        if (participant == null) {
            return false;
        }
        IGameLobby lobby = this.getLobby(participant);
        if (lobby.getParticipants().size() == 1) {
            for (int i = lobby.getBots().size(); i > 0; i--) {
                removeBot(lobby);
            }
            deleteGame(lobby);
            this.lobbyManager.deleteLobby(lobby);
            clear(participant);
            return true;
        }
        if (lobby.remove(participant)) {
            deleteGame(lobby);
            clear(participant);
            return true;
        }
        return false;
    }

    @Override
    public boolean setLobbyHost(String authToken, String targetName) {
        Participant host;
        Participant target;
        try {
            host = getParticipant(userService.getUserFromToken(authToken).getUserId());
            target = getParticipant(userService.getUser(targetName).getUserId());
        }
        catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
            return false;
        }
        if (host == null || target == null) {
            return false;
        }
        if (hasNoPermissions(host, target, Rank.HOST)) {
            return false;
        }
        return getLobby(host).setHost(target);
    }

    /**
     * Will determine whether the invoker has at least the minimum and targets rank.
     *
     * @param invoker the invoker
     * @param target the target
     * @param minimum the minimum required rank
     * @return whether the invoker has a sufficient rank
     */
    private boolean hasNoPermissions(Participant invoker, Participant target, Rank minimum) {
        return invoker.getRank().compareTo(minimum) < 0 || invoker.getRank().compareTo(target.getRank()) < 0;
    }

    @Override
    public boolean setReadyState(String authToken, String state) {
        if (state == null || state.isBlank()) {
            return false;
        }
        long userId = userService.getUserFromToken(authToken).getUserId();
        Participant participant = getParticipant(userId);
        if (participant == null) {
            return false;
        }
        ReadyState readyState;
        try {
            readyState = ReadyState.valueOf(formatEnumValue(state));
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return participant.setReadyState(readyState);
    }

    @Override
    public List<LobbyStomp> getPublicLobbies() {
        List<LobbyStomp> lobbyStomps = new ArrayList<>();
        for (IGameLobby l : lobbyManager.getLobbies(Visibility.PUBLIC)) {
            lobbyStomps.add(new LobbyStomp(l));
        }
        return lobbyStomps;
    }

    @Override
    public boolean isNotInLobby(String authToken, String lobbyCode) {
        Participant participant = this.getParticipant(authToken);
        IGameLobby lobby = lobbyManager.getLobby(lobbyCode);
        if (participant == null || lobby == null) {
            return true;
        }
        return !lobby.getParticipants().contains(participant);
    }


    private Participant createParticipant(String token) {
        long userId = jwtService.extractUserId(token);
        participantsMap.computeIfAbsent(userId, k -> new Participant(userService.getUser(k)));
        return participantsMap.get(userId);
    }

    private Participant getParticipant(String token) {
        return getParticipant(jwtService.extractUserId(token));
    }

    private Participant getParticipant(long userId) {
        return participantsMap.get(userId);
    }

    /**
     * Will get the lobby in which the participant is currently active.
     *
     * @param participant the participant
     * @return the lobby
     * @throws UndesiredStateException when the lobby is null
     */
    private IGameLobby getLobby(Participant participant) {
        IGameLobby lobby = lobbyManager.getLobby(participant);
        if (lobby == null) {
            throw new UndesiredStateException(KNOWN_USER_NO_LOBBY_ERROR);
        }
        return lobby;
    }

    private void clear(Participant participant) {
        participantsMap.remove(participant.getUser().getUserId());
    }

    private String formatEnumValue(String value) {
        return value.trim().replace(" ", "_").toUpperCase();
    }

    private void removeBot(IGameLobby lobby) {
        Participant bot = lobby.popBot();
        User user = (User) bot.getUser();
        userService.deleteUser(user);
    }

    private void deleteGame(IGameLobby lobby) {
        gameService.deleteGame(lobby.getGameId());
        lobby.setGameId("");
    }
}
