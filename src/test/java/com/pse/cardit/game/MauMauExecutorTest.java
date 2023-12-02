package com.pse.cardit.game;

import com.pse.cardit.game.model.player.APlayer;
import com.pse.cardit.game.model.player.IPlayer;
import com.pse.cardit.game.model.player.Player;
import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.executor.MauMauExecutor;
import com.pse.cardit.game.service.controll.InteractResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MauMauExecutorTest {

    //TODO: Test 7 gelegt, falsche Karte legen
    static List<MauMauExecutor> getExecutors() {
        List<MauMauExecutor> executors = new ArrayList<>();
        List<APlayer> players;
        for (int j = 2; j <= 8; j++) {
            players = new ArrayList<>();
            for (int i = 0; i < j; i++) {
                players.add(new Player(i));
            }
            executors.add(new MauMauExecutor("" + j, players, new ArrayList<>()));
        }
        return executors;
    }

    @ParameterizedTest
    @MethodSource("getExecutors")
    void interact(MauMauExecutor executor) {
        System.out.println(executor.getGameState());
        String res;
        List<String> interactions = List.of("getHand",
                "drawFromDrawpile 1",
                "discard S:A",
                "play S:A H");
        for (IPlayer p : executor.getPlayers()) {
            try {
                for (String interact : interactions) {
                    res = executor.interact(p, interact).getResponse();
                    System.out.println(p.getUserID() + "'s " + interact + ": " + res);
                }
            }
            catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @ParameterizedTest
    @MethodSource("getExecutors")
    void getGameState(MauMauExecutor executor) {
        String initialState = executor.getGameState();
        assertFalse(initialState.isBlank(), "If executor was created, it should have initial state");

    }


    @ParameterizedTest
    @MethodSource("getExecutors")
    void getHand(MauMauExecutor executor) {
        for (int i = 0; i < executor.getPlayers().size() * 2; i++) {
            IPlayer activePlayer = executor.getActivePlayer();
            for (IPlayer player : executor.getPlayers()) {
                InteractResponse response = executor.interact(player, "getHand");
                assertTrue(response.getStatus().is2xxSuccessful(), "Should be able to get "
                        + "Hand at any time.");
                assertEquals(GameStatus.CREATED, response.getStatus(), "Since there is something in the response body"
                        + " to handle, status should be created.");
                assertFalse(response.getResponse().isBlank(), "Should have hand cards.");
            }
            assertTrue(executor.interact(activePlayer, "drawFromDrawpile 1").getStatus().is2xxSuccessful(), "Should "
                    + "be able to draw if I am active.");
            assertTrue(executor.interact(activePlayer, "pass").getStatus().is2xxSuccessful(), "Should be able to pass"
                    + " after I have drawn.");
        }
        System.out.println(executor.getGameState());
    }
}