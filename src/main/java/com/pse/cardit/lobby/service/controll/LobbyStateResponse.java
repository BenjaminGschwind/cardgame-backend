package com.pse.cardit.lobby.service.controll;

import com.pse.cardit.lobby.model.IGameLobby;
import com.pse.cardit.lobby.model.Participant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LobbyStateResponse extends LobbyStomp {
    private List<ParticipantStomp> playerList;
    private String visibility;
    private int amountBots;
    private List<ParticipantStomp> botList;
    private String difficulty;
    private int afkTimer;
    private List<String> rules;

    public LobbyStateResponse(IGameLobby lobby) {
        super(lobby.getLobbyCode().toString(), lobby.getGameType().toString(), lobby.getParticipants().size());
        this.playerList = new ArrayList<>();
        for (Participant p : lobby.getParticipants()) {
            playerList.add(new ParticipantStomp(p));
        }
        this.visibility = lobby.getVisibility().toString();
        this.amountBots = lobby.getBots().size();
        this.botList = new ArrayList<>();
        for (Participant p : lobby.getBots()) {
            botList.add(new ParticipantStomp(p));
        }
        this.difficulty = lobby.getDifficulty().toString();
        this.afkTimer = lobby.getAfkTimer();
        this.rules = lobby.getGameSettings();
    }

    public LobbyStateResponse() {
        playerList = new ArrayList<>();
        botList = new ArrayList<>();
        rules = new ArrayList<>();
    }
}
