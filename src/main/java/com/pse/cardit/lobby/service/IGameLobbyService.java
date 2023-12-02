package com.pse.cardit.lobby.service;

import com.pse.cardit.game.service.GameType;
import com.pse.cardit.lobby.service.controll.ChangeSettingsRequest;
import com.pse.cardit.lobby.service.controll.LobbyStateResponse;

/**
 * The interface Game lobby service.
 */
public interface IGameLobbyService extends ILobbyService {
    /**
     * Ein Nutzer, identifiziert über seinen Token, erstellt zu einem Spiel eine private Lobby.
     * Das Lobbyobjekt, welches vom Lobby Manager verwaltet wird, kann genau dann erzeugt werden, wenn die
     * Obergrenze an gleichzeitigen Lobbies nicht überschritten wird. Die Lobby lebt so lange, wie sich ein Spieler
     * darin befindet, dementsprechend wird der User direkt in die Lobby eingefügt. Der eindeutige Lobbycode wird
     * als String zurückgegeben. <br>
     * If a user tries to create a lobby while currently in a lobby, he will leave that lobby first. If and only if
     * creating the new lobby they will attempt to join their old lobby again. In multi threaded application contexts
     * this might mean, that some other action took place for that lobby and rejoining might fail. This might be
     * because for example the lobby was already at capacity, and someone else joined in between.
     *
     * @param token    the authentication token
     * @param gameType the game type
     * @return the lobby code string or empty string if no lobby could be created
     */
    String createLobby(String token, GameType gameType);

    /**
     * Ein Nutzer versucht das ausgewählte Spiel seiner zugehörigen Lobby zu starten. Dafür muss er in einer Lobby
     * sein, deren Eigentümer sowie alle Teilnehmer ihren Bereitschaftsstatus auf 'Bereit' stehen haben. Sind all diese
     * Bedingungen erfüllt, so wird die Game ID zurückgegeben. Sonst wird ein Fehler geworfen.
     *
     * @param authToken the auth token
     * @return the string
     */
    String startGame(String authToken);

    /**
     * Ein Nutzer, identifiziert über seine User Id, versucht die Einstellungen seiner Lobby auf die im
     * Change-Setting-Request spezifizierten zu setzen. Dafür muss er Teil einer Lobby sein und die notwendigen Rechte
     * besitzen, um Änderungen vorzunehmen. Ist dies erfüllt, wird der Zustand der Lobby angepasst und true
     * zurückgegeben.
     *
     * @param authToken             the auth token
     * @param changeSettingsRequest the change settings request
     * @return the boolean
     */
    boolean modifySettings(String authToken, ChangeSettingsRequest changeSettingsRequest);

    /**
     * Erzeugt ein Lobby Zustands Antwort-Objekt mit Daten, welche aus der Lobby mit dem spezifizierten Lobby Code
     * erlangt werden.
     *
     * @param lobbyCode the lobby code
     * @return the lobby state
     * @throws IllegalArgumentException when the lobby code is unknown
     */
    LobbyStateResponse getLobbyState(String lobbyCode);

    /**
     * Gets personal lobby state. Will attempt to get the user from their auth token. If they are currently in any
     * lobby, that lobbies state will be returned.
     *
     * @param token the authorization json web token
     * @return the lobby state of the users lobby
     * @throws IllegalArgumentException if the user is not in any lobby
     */
    LobbyStateResponse getPersonalLobbyState(String token);
}
