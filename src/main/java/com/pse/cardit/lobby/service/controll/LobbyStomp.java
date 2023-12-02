package com.pse.cardit.lobby.service.controll;

import com.pse.cardit.lobby.model.IGameLobby;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LobbyStomp {
    private String lobbyCode;
    private String gameType;
    private int amountPlayers;

    public LobbyStomp(IGameLobby lobby) {
        this.lobbyCode = lobby.getLobbyCode().toString();
        this.gameType = lobby.getGameType().toString();
        this.amountPlayers = lobby.getParticipants().size();
    }
}
