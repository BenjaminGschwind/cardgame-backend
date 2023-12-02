package com.pse.cardit.lobby.service;

import com.pse.cardit.game.service.GameType;
import com.pse.cardit.lobby.model.GameLobby;
import com.pse.cardit.lobby.model.IGameLobby;
import com.pse.cardit.lobby.model.ILobby;
import com.pse.cardit.lobby.model.LobbyCode;
import com.pse.cardit.lobby.model.Participant;
import com.pse.cardit.lobby.model.Visibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GameLobbyManager {

    public static final String UNKNOWN_LOBBY_CODE_ERROR = "Error, the specified lobby code is not registered.";
    private static final int MAXIMUM_CONCURRENT_LOBBIES = 100;

    private static GameLobbyManager instance;
    private final Map<Participant, IGameLobby> lobbies;
    private final Map<LobbyCode, IGameLobby> lobbyMap;

    private GameLobbyManager() {
        this.lobbies = new HashMap<>();
        this.lobbyMap = new HashMap<>();
    }

    public static GameLobbyManager getInstance() {
        if (instance == null) {
            instance = new GameLobbyManager();
        }
        return instance;
    }

    public IGameLobby getLobby(Participant player) {
        if (lobbies.containsKey(player)) {
            if (lobbies.get(player).getParticipants().contains(player)) {
                return lobbies.get(player);
            }
            else {
                lobbies.remove(player);
            }
        }
        for (IGameLobby lobby : lobbyMap.values()) {
            if (lobby.getParticipants().contains(player)) {
                lobbies.put(player, lobby);
                return lobby;
            }
        }
        return null;
    }

    public IGameLobby getLobby(String lobbyCode) {
        return lobbyMap.get(getLobbyCode(lobbyCode));
    }

    public String createLobby(Participant host, GameType gameType) {
        if (lobbyMap.size() > MAXIMUM_CONCURRENT_LOBBIES) {
            return "";
        }
        IGameLobby lobby = new GameLobby(host, gameType, generateLobbyCode());
        lobbies.put(host, lobby);
        lobbyMap.put(lobby.getLobbyCode(), lobby);
        return lobby.getLobbyCode().toString();
    }

    public void deleteLobby(ILobby lobby) {
        if (lobby == null) {
            return;
        }
        lobbyMap.remove(lobby.getLobbyCode());
        for (Participant participant : lobby.getParticipants()) {
            lobbies.remove(participant);
        }
    }

    private LobbyCode generateLobbyCode() {
        LobbyCode lobbyCode = LobbyCode.generateRandom();
        if (lobbyMap.containsKey(lobbyCode)) {
            return generateLobbyCode();
        }
        return lobbyCode;
    }

    private LobbyCode getLobbyCode(String lobbyCode) {
        try {
            return new LobbyCode(lobbyCode);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(UNKNOWN_LOBBY_CODE_ERROR);
        }
    }

    public List<IGameLobby> getLobbies(Visibility visibility) {
        ArrayList<IGameLobby> visibleLobbies = new ArrayList<>();
        for (IGameLobby lobby : lobbyMap.values()) {
            if (lobby.getVisibility() == visibility) {
                visibleLobbies.add(lobby);
            }
        }
        return visibleLobbies;
    }
}
