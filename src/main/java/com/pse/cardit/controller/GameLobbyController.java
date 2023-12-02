package com.pse.cardit.controller;

import com.pse.cardit.game.service.GameType;
import com.pse.cardit.lobby.exceptions.InsufficientLobbyPermissions;
import com.pse.cardit.lobby.service.IGameLobbyService;
import com.pse.cardit.lobby.service.controll.ChangeSettingsRequest;
import com.pse.cardit.lobby.service.controll.CreateLobbyRequest;
import com.pse.cardit.lobby.service.controll.CreateLobbyResponse;
import com.pse.cardit.lobby.service.controll.GetLobbiesResponse;
import com.pse.cardit.lobby.service.controll.KickUserRequest;
import com.pse.cardit.lobby.service.controll.LobbyStateResponse;
import com.pse.cardit.lobby.service.controll.SetReadyStateRequest;
import com.pse.cardit.lobby.service.controll.StartGameRequest;
import com.pse.cardit.lobby.service.controll.StartGameResponse;
import com.pse.cardit.lobby.service.controll.TransferHostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.pse.cardit.WebSocketConfig.WEBSOCKET_PREFIX;

/**
 * The type Game lobby controller.
 */
@RestController
@Controller
@RequiredArgsConstructor
public class GameLobbyController {
    private static final String LOBBY_PREFIX = "/lobby";
    private static final String DEBUG_INFO_FORMAT = "This likely means that some edge case in GameLobbyService "
            + "method %s was not handled!" + System.lineSeparator()
            +  "Request was: %s" + System.lineSeparator() + "Service was: %s";
    private final IGameLobbyService service;
    private final SimpMessagingTemplate simpMessagingTemplate;


    /**
     * Will try to find the user specified by their auth token. If the user exists and is currently in a lobby, that
     * lobbies status will be returned successfully. Otherwise, the request will fail.
     *
     * @param token the authorization header bearer token
     * @return  <strong>200</strong> the lobby state <br>
     *          <strong>404</strong> the user is not in any lobby <br>
     */
    @GetMapping(LOBBY_PREFIX + "/personal/state")
    public ResponseEntity<LobbyStateResponse> getLobbyState(@RequestHeader("Authorization") String token) {
        LobbyStateResponse response;
        try {
            response = service.getPersonalLobbyState(token);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * Will attempt to change the settings of the lobby, specified by its unique lobby code, to those provided in the
     * request.
     *
     * @param request   the {@link ChangeSettingsRequest}
     * @param lobbyCode the lobby code
     */
    @MessageMapping(LOBBY_PREFIX + "/{lobbyCode}/settings")
    public void changeSettings(@Payload ChangeSettingsRequest request, @DestinationVariable String lobbyCode) {
        if (request.getAuthToken() == null) {
            //UNAUTHORIZED
            return;
        }
        if (service.isNotInLobby(request.getAuthToken(), lobbyCode)) {
            //NOT_FOUND
            return;
        }
        boolean isModified;
        try {
            isModified = service.modifySettings(request.getAuthToken(), request);
        }
        catch (IllegalArgumentException e) {
            //BAD_REQUEST
            Logger.getLogger(this.getClass().getName()).log(new LogRecord(Level.WARNING,
                    String.format(DEBUG_INFO_FORMAT, "modifySettings()", request, service)));
            return;
        }
        if (isModified) {
            broadcastLobbyState(lobbyCode);
        }
        //OK
    }

    /**
     * Will attempt to remove a user, specified in the {@link KickUserRequest}. This is successful if and only if
     * both the target and invoking user are in the same lobby and the invokers permissions are sufficient.
     *
     * @param request   the {@link KickUserRequest}
     * @param lobbyCode the lobby code
     */
    @MessageMapping(LOBBY_PREFIX + "/{lobbyCode}/kick")
    public void kickUser(@Payload KickUserRequest request, @DestinationVariable String lobbyCode) {
        if (request.getAuthToken() == null) {
            //UNAUTHORIZED
            return;
        }
        if (service.isNotInLobby(request.getAuthToken(), lobbyCode)) {
            //NOT_FOUND
            return;
        }
        boolean removed;
        try {
            removed = service.removeFromLobby(request.getAuthToken(), request.getTargetUsername());
        }
        catch (InsufficientLobbyPermissions e) {
            //FORBIDDEN
            return;
        }
        if (removed) {
            broadcastLobbyState(lobbyCode);
        }
        //OK
    }

    @MessageMapping(LOBBY_PREFIX + "/{lobbyCode}/transferHost")
    public void transferHost(@Payload TransferHostRequest request, @DestinationVariable String lobbyCode) {
        if (request.getAuthToken() == null) {
            //UNAUTHORIZED
            return;
        }
        if (service.isNotInLobby(request.getAuthToken(), lobbyCode)) {
            //NOT_FOUND
            return;
        }
        boolean transferred;
        try {
            transferred = service.setLobbyHost(request.getAuthToken(), request.getTargetUsername());
        }
        catch (InsufficientLobbyPermissions e) {
            //FORBIDDEN
            return;
        }
        if (transferred) {
            broadcastLobbyState(lobbyCode);
        }
        //OK
    }

    /**
     * Will attempt to start the lobbys game. This is successful if and only if the invoking user, specified by their
     * auth token, has sufficient permissions, and all required internal criteria to start the activity are met.
     *
     * @param request   the {@link StartGameRequest}
     * @param lobbyCode the lobby code
     * @return the {@link StartGameResponse}
     */
    @MessageMapping(LOBBY_PREFIX + "/{lobbyCode}/start/game")
    @SendTo(WEBSOCKET_PREFIX + LOBBY_PREFIX + "/{lobbyCode}/start")
    public StartGameResponse startGame(@Payload StartGameRequest request, @DestinationVariable String lobbyCode) {
        if (request.getAuthToken() == null) {
            //UNAUTHORIZED
            return new StartGameResponse("");
        }
        if (service.isNotInLobby(request.getAuthToken(), lobbyCode)) {
            //NOT_FOUND
            return new StartGameResponse("");
        }
        String gameIdentifier = service.startGame(request.getAuthToken());
        if (!gameIdentifier.isBlank()) {
            //FORBIDDEN
            return new StartGameResponse("");
        }
        //OK
        return new StartGameResponse(gameIdentifier);
    }

    /**
     * Will create a new lobby for the user, identified by their auth token. Creation details are provided in the
     * {@link CreateLobbyRequest}.
     *
     * @param request the {@link CreateLobbyRequest}
     * @param token the authorization header bearer token
     * @return  <strong>200</strong> the {@link CreateLobbyResponse} <br>
     *          <strong>403</strong> if the lobby could not be created. Likely there are too many active lobbies
     *          currently. <br>
     *
     */
    @PostMapping(LOBBY_PREFIX + "/create")
    public ResponseEntity<Void> createLobby(@RequestBody CreateLobbyRequest request,
                                                           @RequestHeader("Authorization") String token) {
        String lobbyCode;
        try {
            lobbyCode = service.createLobby(token, GameType.valueOf(request.getGameType()
                    .toUpperCase().trim().replace(" ", "_")));
        }
        catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(new LogRecord(Level.WARNING,
                    String.format(DEBUG_INFO_FORMAT, "createLobby()", request, service)));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (lobbyCode.isBlank()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Will add a participant identified by their auth token to the lobby specified by its lobby code.
     *
     * @param lobbyCode the lobby code
     * @param token the authorization header bearer token
     * @return  <strong>200</strong> and the lobby state <br>
     *          <strong>403</strong> if the lobby could not be joined <br>
     *          <strong>404</strong> if the lobby was not found <br>
     */
    @PostMapping(LOBBY_PREFIX + "/{lobbyCode}/join")
    public ResponseEntity<Void> joinLobby(@PathVariable String lobbyCode,
                                                        @RequestHeader("Authorization") String token) {
        boolean joined;
        try {
            joined = service.addToLobby(token, lobbyCode);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!joined) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        broadcastLobbyState(lobbyCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Will remove a participant identified by their auth token from their lobby specified by its lobby code.
     *
     * @param lobbyCode the lobby code
     * @param token the authorization header bearer token
     * @return  <strong>200</strong> when successful <br>
     *          <strong>403</strong> the participant was not allowed to leave <br>
     *          <strong>404</strong> the lobby was not found <br>
     */
    @PutMapping(LOBBY_PREFIX + "/{lobbyCode}/leave")
    public ResponseEntity<Void> leaveLobby(@PathVariable String lobbyCode,
                                           @RequestHeader("Authorization") String token) {
        if (service.isNotInLobby(token, lobbyCode)) {
            //FORBIDDEN
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean left;
        try {
            left = service.removeFromLobby(token);
        }
        catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(new LogRecord(Level.WARNING,
                    String.format(DEBUG_INFO_FORMAT, "removeFromLobby()", lobbyCode, service)));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!left) {
            Logger.getGlobal().log(new LogRecord(Level.SEVERE, "Some user was apparently in lobby but unable to "
                    + "leave! This is likely a bug!"));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        broadcastLobbyState(lobbyCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Gets all lobbies with their listing type set to public. This list combined in the {@link GetLobbiesResponse}
     * might grow quite large. Maybe something like lazy loading should be implemented for larger applications.
     *
     * @return the public lobbies
     */
    @GetMapping(LOBBY_PREFIX + "/get/public/lobbies")
    public ResponseEntity<GetLobbiesResponse> getPublicLobbies() {
        return new ResponseEntity<>(new GetLobbiesResponse(service.getPublicLobbies()), HttpStatus.OK);
    }

    /**
     * Sets the ready state of a provided participant that is currently in the lobby specified by its lobby code.
     *
     * @param request   the {@link SetReadyStateRequest}
     * @param lobbyCode the lobby code
     */
    @MessageMapping(LOBBY_PREFIX + "/{lobbyCode}/ready")
    public void setReadyState(@Payload SetReadyStateRequest request, @DestinationVariable String lobbyCode) {
        if (request.getAuthToken() == null) {
            //UNAUTHORIZED
            return;
        }
        if (service.isNotInLobby(request.getAuthToken(), lobbyCode)) {
            //NOT_FOUND
            return;
        }
        boolean setReadyState;
        try {
            setReadyState = service.setReadyState(request.getAuthToken(), request.getReadyState());
        }
        catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(new LogRecord(Level.WARNING,
                    String.format(DEBUG_INFO_FORMAT, "setReadyState()", request, service)));
            //FORBIDDEN
            return;
        }
        if (setReadyState) {
            broadcastLobbyState(lobbyCode);
        }
    }

    /**
     * Will broadcast the state of a given lobby, specified by its unique lobby code, to all clients connected to the
     * websocket message board. <br>
     * This function should be called any time the internal state of the lobby changes.
     *
     * @param lobbyCode the lobby code
     */
    private void broadcastLobbyState(String lobbyCode) {
        Logger.getLogger(this.getClass().getName()).log(new LogRecord(Level.INFO, "Broadcasting Lobby State now!"));
        LobbyStateResponse stateResponse;
        try {
            stateResponse = service.getLobbyState(lobbyCode);
        }
        catch (IllegalArgumentException e) {
            stateResponse = new LobbyStateResponse();
        }
        Logger.getLogger(this.getClass().getName()).log(new LogRecord(Level.INFO,
                stateResponse.toString()));
        simpMessagingTemplate.convertAndSend(WEBSOCKET_PREFIX + LOBBY_PREFIX + "/" + lobbyCode,
                stateResponse);
    }
}