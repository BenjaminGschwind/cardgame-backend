package com.pse.cardit.controller;

import com.pse.cardit.ServiceTest;
import com.pse.cardit.lobby.service.controll.CreateLobbyRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GameLobbyControllerTest extends ServiceTest {
    @Autowired
    private GameLobbyController controller;

    @AfterEach
    void tearDown() {
    }

    @Test
    void changeSettings() {
    }

    @Test
    void kickUser() {
    }

    @Test
    void startGame() {
    }

    @ParameterizedTest
    @MethodSource("getGameTypes")
    void createLobby(String gameType, boolean expected) {
        if (expected) {
            assertTrue(controller.createLobby(new CreateLobbyRequest(gameType), token).getStatusCode().is2xxSuccessful()
                    , "Should be able to create lobby");
            String lobbyCode = controller.getLobbyState(token).getBody().getLobbyCode();
            controller.leaveLobby(lobbyCode, token);
        }
        else {
            assertFalse(controller.createLobby(new CreateLobbyRequest(gameType), token).getStatusCode().is2xxSuccessful()
                    , "Should not be able to create lobby");
        }
    }

    @Test
    void joinLobby() {
    }

    @Test
    void leaveLobby() {
        controller.createLobby(new CreateLobbyRequest("mau mau"), token);
        String lobbyCode = controller.getLobbyState(token).getBody().getLobbyCode();
        assertTrue(controller.leaveLobby(lobbyCode, token).getStatusCode().is2xxSuccessful(), "Should be able to "
                + "leave when alone in own lobby.");
    }

    @Test
    void getPublicLobbies() {
    }

    @Test
    void setReadyState() {
    }

    @Test
    void getLobbyState() {
        assertSame( HttpStatus.NOT_FOUND, controller.getLobbyState(token).getStatusCode(),"Not in lobby yet, "
                + "thus should not have state to get.");
        controller.createLobby(new CreateLobbyRequest("mau mau"), token);
        assertTrue(controller.getLobbyState(token).getStatusCode().is2xxSuccessful(), "In lobby, so should get "
                + "something");
        assertNotNull(controller.getLobbyState(token).getBody(), "This something should definitely exist lol.");
        String lobbyCode = controller.getLobbyState(token).getBody().getLobbyCode();
        controller.leaveLobby(lobbyCode, token);
    }

    private static List<Arguments> getGameTypes() {
        return List.of(
                Arguments.of("MAU_MAU", true),
                Arguments.of("mau mau", true),
                Arguments.of(" mau mau", true),
                Arguments.of("mau mau ", true),
                Arguments.of("mAu MaU", true),
                Arguments.of("mau_mau", true),
                Arguments.of("maexxle", true),
                Arguments.of("schwimmen", true),
                Arguments.of("RäuberHotzenplotz", false),
                Arguments.of("_MAU_MAU", false),
                Arguments.of(". mau mau", false)
        );
    }
}