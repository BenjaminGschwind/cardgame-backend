package com.pse.cardit.lobby;

import com.pse.cardit.ServiceTest;
import com.pse.cardit.game.model.player.DifficultyEnum;
import com.pse.cardit.game.service.GameService;
import com.pse.cardit.game.service.GameType;
import com.pse.cardit.lobby.model.GameLobby;
import com.pse.cardit.lobby.model.Rank;
import com.pse.cardit.lobby.model.Visibility;
import com.pse.cardit.lobby.service.IGameLobbyService;
import com.pse.cardit.lobby.service.controll.ChangeSettingsRequest;
import com.pse.cardit.lobby.service.controll.LobbyStateResponse;
import com.pse.cardit.lobby.service.controll.ParticipantStomp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ILobbyServiceTest extends ServiceTest {

    @Autowired
    private IGameLobbyService service;
    @Autowired
    private GameService gameService;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @ParameterizedTest
    @EnumSource(GameType.class)
    void createLobbyGameType(GameType gameType) {
        assertFalse(service.createLobby(token, gameType).isEmpty(), "Should be able to create lobby for game "
                + "type " + gameType.toString());
        assertTrue(service.removeFromLobby(token), "Should be able to remove, since that user just created the "
                + "lobby is the only one in lobby");
    }

    @Test
    void createLobby() {
        String lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        assertFalse(lobbyCode.isBlank(), "Should be able to create lobby");
        service.modifySettings(token, getVis(Visibility.PUBLIC));
        assertEquals(1, service.getPublicLobbies().size(), "Should have exactly one lobby now");
        lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        assertFalse(lobbyCode.isBlank(), "Should be able to create lobby");
        service.modifySettings(token, getVis(Visibility.PUBLIC));
        assertEquals(1, service.getPublicLobbies().size(), "Should still have exactly one lobby");

    }

    @Test
    void startGameBot() {
        String lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        service.setReadyState(token, "READY");
        ChangeSettingsRequest request = copy(service.getLobbyState(lobbyCode));
        request.setAmountBots(0);
        service.modifySettings(token, request);
        assertTrue(service.startGame(token).isEmpty(), "Host should not be able to start game because he is alone");
        request.setAmountBots(1);
        service.modifySettings(token, request);
        assertFalse(service.startGame(token).isEmpty(), "Host should be able to start game");
        service.removeFromLobby(token);
        gameService.resetService();
    }

    @Test
    void startGamePlayer() {
        String lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        service.setReadyState(token, "READY");
        String token2 = super.getToken("SecondTester", "nope");
        service.addToLobby(token2, lobbyCode);
        service.setReadyState(token2, "READY");
        assertTrue(service.startGame(token2).isEmpty(), "Memeber should not be able to start game");
        assertFalse(service.startGame(token).isEmpty(), "Host should be able to start game");
        assertTrue(service.startGame(token2).isEmpty(), "Memeber should still not be able to start game");
        assertFalse(service.startGame(token).isEmpty(), "Host should be able to start game for a second time");
        service.removeFromLobby(token2);
        service.removeFromLobby(token);
        gameService.resetService();
    }

    @Test
    void modifySettings() {
        String code = service.createLobby(token, GameType.MAU_MAU);
        ChangeSettingsRequest request = new ChangeSettingsRequest(
                token, "MAEXXLE", "PUBLIC", "EASY", 2, 60, new ArrayList<>()
        );
        assertFalse(service.getPublicLobbies().toString().contains(code),
                "The lobby is still private so it shouldn't be listed");
        assertTrue(service.modifySettings(token, request), "User is host, so should change settings");
        assertTrue(service.getPublicLobbies().toString().contains(code),
                "The lobby is now public so it should be listed");
        service.getPersonalLobbyState(token);
        System.out.println(service.getPersonalLobbyState(token));
        System.out.println(service.getPublicLobbies().toString());
    }


    @ParameterizedTest
    @EnumSource(GameType.class)
    void getLobbyStateGameChange(GameType initial) {
        String lobbyCode = service.createLobby(token, initial);
        LobbyStateResponse state, oldState;
        assertDoesNotThrow(() -> service.getLobbyState(lobbyCode), "Should be able to get state of new lobby!");
        state = service.getLobbyState(lobbyCode);
        System.out.println(state.toString());
        for (GameType gameType : GameType.values()) {
            if (gameType != initial) {
                oldState = state;
                service.modifySettings(token, getGame(gameType));
                state = service.getLobbyState(lobbyCode);
                assertNotEquals(oldState.getGameType(), state.getGameType(), "Should have changed because game type was "
                        + "modified");
            }
        }
    }

    @ParameterizedTest
    @EnumSource(Visibility.class)
    void getLobbyStateVisibilityChange(Visibility visibility) {
        String lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        LobbyStateResponse state, oldState;
        assertDoesNotThrow(() -> service.getLobbyState(lobbyCode), "Should be able to get state of new lobby!");
        for (Visibility initial : Visibility.values()) {
            service.modifySettings(token, getVis(initial));
            state = service.getLobbyState(lobbyCode);
            System.out.println(state.toString());
            if (visibility != initial) {
                oldState = state;
                service.modifySettings(token, getVis(visibility));
                state = service.getLobbyState(lobbyCode);
                System.out.println(state);
                assertNotEquals(oldState.getVisibility(), state.getVisibility(), "Should have changed because visibility was "
                        + "modified");
            }
        }
    }

    @Test
    void getLobbyState() {
        String lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        LobbyStateResponse state = service.getLobbyState(lobbyCode);
        ChangeSettingsRequest request = copy(state);
        List<Integer> botAmounts = new ArrayList<>();
        if (state.getAmountBots() == 0) {
            botAmounts.add(1);
            request.setAmountBots(1);
        }
        else {
            botAmounts.add(0);
            request.setAmountBots(0);
        }
        botAmounts.add(2);
        botAmounts.add(0);
        for (int i : botAmounts) {
            request.setAmountBots(i);
            service.modifySettings(token, request);
            state = service.getLobbyState(lobbyCode);
            assertEquals(request.getAmountBots(), state.getAmountBots(), "Should be changed bot amount");
            assertEquals(request.getAmountBots(), state.getBotList().size(), "Should be changed bot amount");
            for (ParticipantStomp p : state.getBotList()) {
                for (ParticipantStomp q : state.getBotList()) {
                    if (p != q) {
                        assertNotEquals(p, q, "Should be different bots...");
                    }
                }
            }
            System.out.println(state);
        }
        if (state.getAfkTimer() == 0) {
            request.setAfkTimer(1);
        }
        else {
            request.setAfkTimer(0);
        }
        service.modifySettings(token, request);
        state = service.getLobbyState(lobbyCode);
        assertEquals(request.getAfkTimer(), state.getAfkTimer(), "Should be changed afk  timer");
    }

    @Test
    void addToLobby() {
    }

    @Test
    void removeFromLobby() {
        service.createLobby(token, GameType.MAU_MAU);
        assertTrue(service.removeFromLobby(token), "Should be able to leave");
        assertThrows(IllegalArgumentException.class, () -> service.getPersonalLobbyState(token), "Should not be in "
                + "lobby");
        String lobbyCode = service.createLobby(token, GameType.MAEXXLE);
        String token2 = super.getToken("SuperCreativeTester", "test");
        service.addToLobby(token2, lobbyCode);
        assertTrue(service.removeFromLobby(token2), "Should be able to leave again");
        assertFalse(service.removeFromLobby(token2), "Already left lobby");
        service.addToLobby(token2, lobbyCode);
        assertTrue(service.removeFromLobby(token2), "Should be able to leave again");
        service.addToLobby(token2, lobbyCode);
        service.createLobby(token2, GameType.MAU_MAU);
        assertEquals(1, service.getLobbyState(lobbyCode).getPlayerList().size(), "Only one player should be left "
                + "here, why was the other not removed?");
        assertTrue(service.removeFromLobby(token2), "Should be able to leave own lobby after having been in one "
                + "before");
        service.addToLobby(token2, lobbyCode);
    }

    @Test
    void rejoinLobby() {
        String lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        String token2 = super.getToken("MyTest", "test");
        assertTrue(service.addToLobby(token2, lobbyCode), "Should be able to join first time");
        assertTrue(service.removeFromLobby(token2), "Should be able to leave");
        assertTrue(service.addToLobby(token2, lobbyCode), "Should be able to rejoin.");
    }

    @ParameterizedTest
    @MethodSource("getNameLists")
    void testRemoveFromLobby(List<String> participants) {
        String password = "none";
        String lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        assertEquals(1, service.getLobbyState(lobbyCode).getAmountPlayers(), "Assuming that there is no one else in "
                + "this new lobby besides the host. Are you sure this should not be the case?");
        for (String name : participants) {
            String uToken = super.getToken(name, password);
            assertTrue(service.addToLobby(uToken, lobbyCode), "If this fails your addToLobby test case is useless!");
            assertFalse(service.removeFromLobby(uToken, SUPER_USERNAME), "Other player should not kick host");
            assertTrue(service.removeFromLobby(token, name), "Host should be able to kick from lobby");
            assertEquals(1, service.getLobbyState(lobbyCode).getAmountPlayers(), "If the second player was kicked "
                    + "there should be just the host again");
        }
        for (int i = 0; i < GameLobby.MAXIMUM_PARTICIPANTS  - 1 && i < participants.size(); i++) {
            String uToken = super.getToken(participants.get(i), password);
            assertTrue(service.addToLobby(uToken, lobbyCode), "If this fails your addToLobby test case is useless!");
        }
        List<ParticipantStomp> players = service.getLobbyState(lobbyCode).getPlayerList();

        for (int i = 0; i < GameLobby.MAXIMUM_PARTICIPANTS  - 1 && i < participants.size(); i++) {
            assertTrue(service.removeFromLobby(token, participants.get(i)), "Host should be able to kick "
                    + "from lobby");
        }
        assertEquals(1, service.getLobbyState(lobbyCode).getAmountPlayers(), "If all players were kicked again it "
                + "should just be the host left.");
    }

    @Test
    void setReadyState() {
        service.createLobby(token, GameType.MAU_MAU);
        assertFalse(service.setReadyState(token, "NOT_READY"), "Initial state should be NOT_READY");
        assertTrue(service.setReadyState(token, "READY"), "Initial state is NOT_READY, so it should change"
                + " now");
        assertFalse(service.setReadyState(token, "READY"),"Nothing changed since state is already set");
        assertTrue(service.setReadyState(token, "NOT_READY"), "Should be able to change back");
        assertTrue(service.setReadyState(token, "rEaDy"),"Capitalization should be ignored");
        assertTrue(service.setReadyState(token, "NOT READY"), "Blank space should not matter");
        assertTrue(service.setReadyState(token, "READY            "), "Trailing spaces should be ignored");
        assertTrue(service.setReadyState(token, "         NOT READY"), "Leading spaces should be ignored");
        assertFalse(service.setReadyState(token, ""), "Illegal input should not change anything");
        assertFalse(service.setReadyState(token, null), "Illegal input should not change anything");
        assertFalse(service.setReadyState(token, "         "), "Illegal input should not change anything");
        assertFalse(service.setReadyState(token, "ASKDH"), "Illegal input should not change anything");
        assertFalse(service.setReadyState(token, "MAYBE"), "Illegal input should not change anything");
        assertFalse(service.setReadyState(token, "READY_READY"), "Illegal input should not change anything");
        service.removeFromLobby(token);
    }

    @Test
    void getPublicLobbies() {
        assertTrue(service.getPublicLobbies().isEmpty(), "No lobbies should be here yet");
        List<String> lobbyCodes = new ArrayList<>();
        List<String> tokens = new ArrayList<>();
        String token, lobbyCode;
        for (GameType type : GameType.values()) {
            token = getToken(type.toString(), type.toString());
            tokens.add(token);
            lobbyCode = service.createLobby(token, type);
            lobbyCodes.add(lobbyCode);
            ChangeSettingsRequest request = getVis(Visibility.PUBLIC);
            request.setGameType(type.toString());
            service.modifySettings(token, request);
            System.out.println(service.getPublicLobbies());
            assertTrue(service.getPublicLobbies().toString().contains(type.toString()), "Lobby is now public, so that"
                    + " way it should be visible...");

        }
        for (String tkn : tokens) {
            service.removeFromLobby(tkn);
        }
        System.out.println(service.getPublicLobbies());
        assertTrue(service.getPublicLobbies().isEmpty(), "All lobbies should be removed now");
    }

    @Test
    void getPersonalLobbyState() {
        service.createLobby(token, GameType.MAU_MAU);
        assertDoesNotThrow(() -> service.getPersonalLobbyState(token), "Token is valid and user in lobby");
        System.out.println(service.getPersonalLobbyState(token));
        service.removeFromLobby(token);
    }

    @Test
    void transferHost() {
        String lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        String member = "TestMember";
        String token2 = super.getToken(member, "test");
        service.addToLobby(token2, lobbyCode);
        System.out.println(service.getLobbyState(lobbyCode));
        assertTrue(service.setLobbyHost(token, member), "Should be able to change host");
        LobbyStateResponse state = service.getLobbyState(lobbyCode);
        ParticipantStomp newHost = null;
        int amountHosts = 0;
        for (ParticipantStomp p : state.getPlayerList()) {
            if (p.getUsername().equals(member)) {
                newHost = p;
            }
            if (p.getRank().equals(Rank.HOST.toString())) {
                amountHosts++;
            }
        }
        assertNotNull(newHost, "Should have been found");
        assertEquals(Rank.HOST.toString(), newHost.getRank(), "Should be host now that it has changed.");
        assertEquals(1, amountHosts, "There should only be one host");
        ChangeSettingsRequest req = copy(state);
        req.setAmountBots(1);
        assertTrue(service.modifySettings(token2, req), "Should now have perms to do that");
        assertFalse(service.setLobbyHost(token2, "Bot 1"), "Can not make Bot host");
    }

    @Test
    void transferHostRejoin() {
        String lobbyCode = service.createLobby(token, GameType.MAU_MAU);
        String member = "TestMember";
        String token2 = super.getToken(member, "test");
        assertTrue(service.addToLobby(token2, lobbyCode));
        System.out.println("Before state");
        System.out.println(service.getLobbyState(lobbyCode));
        assertTrue(service.setLobbyHost(token, member), "Should be able to change host");
        service.removeFromLobby(token);
        assertTrue(service.addToLobby(token, lobbyCode), "Should be able to rejoin");
        assertTrue(service.setLobbyHost(token2, SUPER_USERNAME), "Should be able to become host once more");
    }

    private static List<List<String>> getNameLists() {
        return List.of(
          List.of("Hans", "Peter", "Müller", "Quade", "DurchBruchMüller", "Hello", "World")
        );
    }

    private ChangeSettingsRequest getVis(Visibility visibility) {
        ChangeSettingsRequest request = new ChangeSettingsRequest();
        request.setVisibility(visibility.toString());
        request.setGameType(GameType.MAU_MAU.toString());
        request.setDifficulty(DifficultyEnum.EASY.toString());
        request.setRules(new ArrayList<>());
        return request;
    }

    private ChangeSettingsRequest getGame(GameType gameType) {
        ChangeSettingsRequest request = new ChangeSettingsRequest();
        request.setVisibility(Visibility.PRIVATE.toString());
        request.setGameType(gameType.toString());
        request.setDifficulty(DifficultyEnum.EASY.toString());
        request.setRules(new ArrayList<>());
        return request;

    }

    private ChangeSettingsRequest copy(LobbyStateResponse state) {
        ChangeSettingsRequest request = new ChangeSettingsRequest();
        request.setRules(state.getRules());
        request.setVisibility(state.getVisibility());
        request.setGameType(state.getGameType());
        request.setDifficulty(state.getDifficulty());
        request.setAfkTimer(state.getAfkTimer());
        request.setAmountBots(state.getAmountBots());
        return request;
    }
}