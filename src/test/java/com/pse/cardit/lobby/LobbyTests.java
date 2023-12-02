package com.pse.cardit.lobby;

import com.pse.cardit.game.service.GameType;
import com.pse.cardit.lobby.model.GameLobby;
import com.pse.cardit.lobby.model.IGameLobby;
import com.pse.cardit.lobby.model.LobbyCode;
import com.pse.cardit.lobby.model.Participant;
import com.pse.cardit.lobby.service.GameLobbyManager;
import com.pse.cardit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LobbyTests {
    private Participant host;
    private GameLobby lobby;
    @BeforeEach
    void setup() {
        host = new Participant(new User(""));
        lobby = new GameLobby(host, GameType.MAU_MAU, LobbyCode.generateRandom());
    }
    @Test
    void lobbyAddTest() {
        Participant[] participants = new Participant[12];
        for (int i = 0; i < participants.length; i++) {
            participants[i] = new Participant(new User(i));
        }
        Participant host = participants[0];
        GameLobbyManager manager = GameLobbyManager.getInstance();
        String s = manager.createLobby(host, GameType.MAU_MAU);
        IGameLobby lobby = manager.getLobby(s);
        for (int i = 1; i < participants.length; i++) {
            for (int j = 0; j < i; j++) {
                assertFalse(lobby.add(participants[j]), "Should not add participants that are already present");
            }
            if (i < lobby.getParticipantLimit()) {
                System.out.println(lobby);
                assertTrue(lobby.add(participants[i]), "Should add participants that are not present");
            }
            else {
                assertFalse(lobby.add(participants[i]), "Should not add participants when full");
            }
        }
        System.out.println(lobby);
    }

    @Test
    void addSelfToLobby() {
        assertTrue(lobby.getParticipants().contains(host), "Host should be part of lobby");
        assertFalse(lobby.add(host), "Nothing should be modified since host is already part of lobby");
    }
}
