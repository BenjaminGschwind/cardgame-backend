package com.pse.cardit.game;

import com.pse.cardit.ServiceTest;
import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.GameType;
import com.pse.cardit.game.service.IGameService;
import com.pse.cardit.game.service.controll.InteractResponse;
import com.pse.cardit.user.service.IUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import com.pse.cardit.lobby.service.IGameLobbyService;
import com.pse.cardit.lobby.service.controll.ChangeSettingsRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GameServiceTest extends ServiceTest {
    @Autowired
    private IGameService service;
    @Autowired
    private IGameLobbyService lobbyService;
    @Autowired
    private IUserService userService;

    private String gameId;
    private long userId;
    private long botId;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        userId = userService.getUserFromToken(token).getUserId();
        botId = userService.addBot("Botters").getUserId();
        gameId = "";

    }

    @AfterEach
    void tearDown() {
        service.deleteGame(gameId);
        System.out.println("Tearing down " + service);
    }
    @Test
    void getState() {
        gameId = service.createGame(new long[] {userId, botId}, GameType.MAU_MAU, new HashMap<>());
        assertDoesNotThrow(() -> service.getState(gameId), "Should be able to get state");
        String state = service.getState(gameId);
        System.out.println(state);
    }

    @Test
    void interact() {
        gameId = service.createGame(new long[] {userId, botId}, GameType.MAU_MAU, new HashMap<>());
        assertFalse(service.interact(token, "getHand").getResponse().isBlank(), "Maybe the game was not setup as a "
                + "game with 'getHand' but that's not likely. You probably made the error elsewhere!");
    }

    @Test
    void createGame() {
        lobbyService.createLobby(token, GameType.MAEXXLE);
        ChangeSettingsRequest request = new ChangeSettingsRequest(token, "MAEXXLE", "PRIVATE",
                "EASY", 3, 0, new ArrayList<>());
        lobbyService.modifySettings(token, request);
        lobbyService.setReadyState(token, "READY");
        gameId = lobbyService.startGame(token);
        assertFalse(gameId.isEmpty(), "Should be able to start game");
        System.out.println(service.interact(token, "rollDice"));
    }

    @Test
    void createGameWithBots() {
        lobbyService.createLobby(token, GameType.MAU_MAU);
        ChangeSettingsRequest request = new ChangeSettingsRequest(token, "MAU_MAU", "PRIVATE",
                "EASY", 3, 0, new ArrayList<>());
        lobbyService.modifySettings(token, request);
        lobbyService.setReadyState(token, "READY");
        gameId = lobbyService.startGame(token);
        assertFalse(gameId.isEmpty(), "Should be able to start game");
        String state = service.getState(gameId);
        System.out.println(state);
    }

    @Test
    void botInteraction() {
        lobbyService.createLobby(token, GameType.MAU_MAU);
        ChangeSettingsRequest request = new ChangeSettingsRequest(token, "MAU_MAU", "PRIVATE",
                "EASY", 3, 0, new ArrayList<>());
        lobbyService.modifySettings(token, request);
        lobbyService.setReadyState(token, "READY");
        gameId = lobbyService.startGame(token);
        String state = service.getState(gameId);
        System.out.println(state);
        InteractResponse result = service.interact(token, "drawFromDrawpile 1");
        System.out.println("Result was " + result.getResponse());
        System.out.println("What is happening?");
        result = service.interact(token, "pass");
        System.out.println("Cheers.");
        assertTrue(result.getStatus().is2xxSuccessful(), "Should be able to pass");
        assertNotEquals(service.getState(gameId), state, "There should be quite some changes!");
        System.out.println(service.getState(gameId));

    }

    @Test
    void endGame() {
        lobbyService.createLobby(token, GameType.MAU_MAU);
        ChangeSettingsRequest request = new ChangeSettingsRequest(token, "MAU_MAU", "PRIVATE",
                "EASY", 3, 0, new ArrayList<>());
        lobbyService.modifySettings(token, request);
        lobbyService.setReadyState(token, "READY");
        gameId = lobbyService.startGame(token);
        String state = service.getState(gameId);
        InteractResponse response;
        while (state.contains("\"gameFinished\" : false")) {
            response = service.interact(token, "drawFromDrawpile 1");
            if (response.getStatusCode() != GameStatus.CREATED.getStatusCode()) {
                System.out.println(response.getResponse());
                System.out.println(state);
            }
            assertTrue(response.getStatus().is2xxSuccessful(), "Should be able to draw if game is not finished"
                    + " yet");
            state = service.getState(gameId);
            response = service.interact(token, "pass");
            if (response.getStatusCode() != GameStatus.CREATED.getStatusCode()) {
                System.out.println(response.getResponse());
                System.out.println(state);
            }
            do {
                state = service.getState(gameId);
                //System.out.println(state);
                if (state.contains("\"gameFinished\" : false")) {
                    assertFalse(response.getResponse().contains("\"cardCount\" : 0"), "If a player has no cards left, why"
                            + " is the game still running?");
                }
            }
            while (service.botInteraction(gameId));

        }
        System.out.println(state);

    }
    @Test
    void deleteGame() {
        assertDoesNotThrow(() -> service.deleteGame(gameId), "Why should this throw an error if the game "
                + "actually exists?");
        assertDoesNotThrow(() -> service.deleteGame(""), "Empty gameId should not cause errors");
        assertDoesNotThrow(() -> service.deleteGame("yxhchai213eu8asc"), "Random letters and numbers");
        assertDoesNotThrow(() -> service.deleteGame(")($asjdk?!'§naojsoijo"), "Random chars with special"
                + " characters");
        assertDoesNotThrow(() -> service.deleteGame("\uD83D\uDE0B Get Emoji "), "Unicode source should "
                + "be okay to use");
        assertDoesNotThrow(() -> service.deleteGame(null), "Null source should be okay");
    }

    @Test
    void getPersonalState() {
        gameId = service.createGame(new long[] {userId, botId}, GameType.MAU_MAU, new HashMap<>());
        assertDoesNotThrow(() -> service.getPersonalState(token), "After setup that user is in game, so it should "
                + "have a state");
        System.out.println(service.getPersonalState(token));
    }
}