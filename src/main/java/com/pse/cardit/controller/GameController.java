package com.pse.cardit.controller;

import com.pse.cardit.game.service.GameStatus;
import com.pse.cardit.game.service.IGameService;
import com.pse.cardit.game.service.controll.GameChannelResponse;
import com.pse.cardit.game.service.controll.GameStateResponse;
import com.pse.cardit.game.service.controll.InteractResponse;
import com.pse.cardit.game.service.controll.InteractionRequest;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.pse.cardit.WebSocketConfig.WEBSOCKET_PREFIX;

@Controller
@RequiredArgsConstructor
public class GameController {
    private static final Random RANDOM = new Random();
    private static final String GAME_PREFIX = "/game";
    private final IGameService service;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @GetMapping("/game/personal/state")
    public ResponseEntity<GameStateResponse> getGameState(@RequestHeader("Authorization") String token) {
        GameStateResponse response;
        try {
            response = service.getPersonalState(token);
        }
        catch (JwtException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(GAME_PREFIX + "/personal/channel")
    public ResponseEntity<GameChannelResponse> getGameChannel(@RequestHeader("Authorization") String token) {
        GameChannelResponse response;
        try {
            response = service.getPersonalChannel(token);
        }
        catch (JwtException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @MessageMapping(GAME_PREFIX + "/{gameId}/interact")
    public InteractResponse interact(@Payload InteractionRequest request, @DestinationVariable String gameId)
        throws InterruptedException {
        if (request.getAuthToken() == null) {
            return new InteractResponse(GameStatus.UNAUTHORIZED, "Error - No authentication token provided!");
        }
        if (service.isNotInGame(request.getAuthToken(), gameId)) {
            return new InteractResponse(GameStatus.BAD_REQUEST, "Error - Currently not in the game!");
        }
        InteractResponse result;
        try {
            result = service.interact(request.getAuthToken(), request.getInteraction());
        }
        catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(new LogRecord(Level.WARNING, "Either you did not do what "
                    + "you said you would, or some edge case was not handled in GameService.interact()."
                    + System.lineSeparator() + "Request was for game " + gameId + ": " + request
                    + System.lineSeparator() + "Service was: " + service));
            return new InteractResponse(GameStatus.BAD_REQUEST, "Error - This is your fault, probably at least. However"
                    + " the server did not anticipate you'd do something like that. Congrats.");
        }
        if (result.getStatus().is2xxSuccessful()) {
            broadcastGameState(gameId);

            while (service.botInteraction(gameId)) {
                //Thread.sleep(RANDOM.nextInt(1000) + 500);
                broadcastGameState(gameId);
            }
        }
        simpMessagingTemplate.convertAndSend(WEBSOCKET_PREFIX + GAME_PREFIX + "/" + gameId + "/interact/"
                + service.getPersonalChannel(request.getAuthToken()).getChannelId(), result);
        return result;
    }

    private void broadcastGameState(@DestinationVariable String gameId) {
        simpMessagingTemplate.convertAndSend(WEBSOCKET_PREFIX + GAME_PREFIX + "/" + gameId,
                new GameStateResponse((service.getState(gameId))));
    }
}
