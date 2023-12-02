package com.pse.cardit.lobby.model;

import com.pse.cardit.game.model.player.DifficultyEnum;
import com.pse.cardit.game.service.GameType;
import com.pse.cardit.lobby.exceptions.LobbyWithoutHostException;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class GameLobby implements IGameLobby {

    public static final int MAXIMUM_PARTICIPANTS = 8;
    private static final String LOBBY_CAN_NOT_BE_EMPTY_ERROR = "Error, Lobby can not be empty!";
    private static final String NO_BOT_LEFT_ERROR = "Error - There was no bot left to pop!";

    private GameType gameType;
    private String gameId;
    private Visibility visibility;
    private final LobbyCode lobbyCode;
    private final List<Participant> participants;
    private final Deque<Participant> bots;
    private DifficultyEnum difficulty;
    private int afkTimer;
    private boolean isActive;
    private final List<String> gameSettings;

    public GameLobby(Participant host, GameType gameType, LobbyCode lobbyCode) {
        this.lobbyCode = lobbyCode;
        this.participants = new ArrayList<>(List.of(host));
        host.setRank(Rank.HOST);
        this.gameType = gameType;
        this.gameId = "";
        this.visibility = Visibility.PRIVATE;
        this.bots = new LinkedList<>();
        this.difficulty = DifficultyEnum.MEDIUM;
        this.afkTimer = 0;
        this.isActive = false;
        this.gameSettings = new ArrayList<>();
    }

    @Override
    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    @Override
    public GameType getGameType() {
        return this.gameType;
    }

    @Override
    public boolean pushBot(Participant participant) {
        if (this.bots.size() + this.participants.size() < MAXIMUM_PARTICIPANTS) {
            this.bots.push(participant);
            participant.setRank(Rank.NONE);
            participant.setReadyState(ReadyState.READY);
            return true;
        }
        return false;
    }

    @Override
    public Participant popBot() {
        if (this.bots.isEmpty()) {
            throw new NoSuchElementException(NO_BOT_LEFT_ERROR);
        }
        return this.bots.pop();
    }

    @Override
    public List<Participant> getBots() {
        return new ArrayList<>(this.bots);
    }

    @Override
    public void setDifficulty(DifficultyEnum difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public DifficultyEnum getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public String getGameId() {
        return gameId;
    }

    @Override
    public boolean changeGameSetting(String rule) {
        return false;
    }

    @Override
    public List<String> getGameSettings() {
        return new ArrayList<>(this.gameSettings);
    }

    @Override
    public boolean add(Participant participant) {
        if (this.participants.size() + this.bots.size() >= MAXIMUM_PARTICIPANTS) {
            return false;
        }
        if (this.isActive) {
            return false;
        }
        if (this.participants.contains(participant)) {
            return false;
        }
        participant.setRank(Rank.NONE);
        participant.setReadyState(ReadyState.NOT_READY);
        return this.participants.add(participant);
    }

    @Override
    public boolean remove(Participant participant) {
        if (!participants.remove(participant)) {
            return false;
        }
        if (participants.isEmpty()) {
            throw new LobbyWithoutHostException(LOBBY_CAN_NOT_BE_EMPTY_ERROR);
        }
        if (participant.getRank() == Rank.HOST) {
            participant.setRank(Rank.NONE);
            transferHost(participants.get(0));
        }
        return true;
    }

    @Override
    public boolean setHost(Participant participant) {
        if (!this.participants.contains(participant)) {
            return false;
        }
        transferHost(participant);
        return true;
    }

    @Override
    public List<Participant> getParticipants() {
        return new ArrayList<>(participants);
    }

    @Override
    public LobbyCode getLobbyCode() {
        return this.lobbyCode;
    }

    @Override
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void setAfkTimer(int afkTimer) {
        this.afkTimer = afkTimer;
    }

    @Override
    public int getAfkTimer() {
        return this.afkTimer;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public int getParticipantLimit() {
        return MAXIMUM_PARTICIPANTS;
    }

    private void transferHost(Participant participant) {
        for (Participant p : participants) {
            if (p.getRank() == Rank.HOST) {
                p.setRank(Rank.NONE);
            }
        }
        participant.setRank(Rank.HOST);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameLobby gameLobby = (GameLobby) o;
        return lobbyCode.equals(gameLobby.lobbyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lobbyCode);
    }

    @Override
    public String toString() {
        return "GameLobby{"
                + "gameType=" + gameType
                + ", gameId='" + gameId + '\''
                + ", visibility=" + visibility
                + ", lobbyCode=" + lobbyCode
                + ", participants=" + participants
                + ", bots=" + bots
                + ", afkTimer=" + afkTimer
                + ", isActive=" + isActive
                + ", gameSettings=" + gameSettings
                + '}';
    }
}
