package com.pse.cardit.lobby;

import com.pse.cardit.game.service.GameType;
import com.pse.cardit.lobby.model.IGameLobby;
import com.pse.cardit.lobby.model.Participant;
import com.pse.cardit.lobby.service.GameLobbyManager;
import com.pse.cardit.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameLobbyManagerTest {

    private final GameLobbyManager manager = GameLobbyManager.getInstance();
    private final Participant host = new Participant(new User(0));
    private String lobbyCode;
    @BeforeEach
    void setUp() {
        lobbyCode = manager.createLobby(host, GameType.MAU_MAU);
    }

    @AfterEach
    void tearDown() {
        manager.deleteLobby(manager.getLobby(lobbyCode));
    }

    @Test
    void getLobbyFromHost() {
        assertNotNull(manager.getLobby(host), "Its their lobby, so you should get it.");
    }

    @Test
    void getLobbyFromMember() {
        IGameLobby lobby = manager.getLobby(lobbyCode);
        Participant member = new Participant(new User(1));
        lobby.add(member);
        assertNotNull(manager.getLobby(member), "They joined, so you should get it.");
    }

    @Test
    void getLobbyFromUnknown() {
        IGameLobby lobby = manager.getLobby(lobbyCode);
        Participant member = new Participant(new User(1));
        lobby.add(member);
        lobby.remove(member);
        assertNull(manager.getLobby(member), "They left, so you shouldn't get it.");
        assertNull(manager.getLobby(new Participant(new User(2))), "They were just created, why should they be in a "
                + "lobby?");
        manager.deleteLobby(lobby);
        assertNull(manager.getLobby(host), "Their lobby was just deleted. Nothing should be found.");
    }

    @Test
    void getLobbyFromMigratedMember() {
        IGameLobby lobby = manager.getLobby(lobbyCode);
        Participant member = new Participant(new User(1));
        String otherCode = manager.createLobby(member, GameType.MAU_MAU);
        Participant otherMember = new Participant(new User(2));
        manager.getLobby(otherCode).add(otherMember);
        manager.getLobby(otherCode).remove(otherMember);
        lobby.add(otherMember);
        assertEquals(lobby, manager.getLobby(otherMember), "They are in there, so you should get it.");
        manager.deleteLobby(manager.getLobby(otherCode));
        lobby.add(member);
        assertEquals(lobby, manager.getLobby(member), "They are in there, so you should get it.");

    }
}