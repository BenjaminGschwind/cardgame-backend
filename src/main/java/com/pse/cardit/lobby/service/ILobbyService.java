package com.pse.cardit.lobby.service;

import com.pse.cardit.lobby.service.controll.LobbyStomp;

import java.util.List;

/**
 * The interface Lobby service.
 */
public interface ILobbyService {
    /**
     * Ein Nutzer tritt der Lobby bei, welche über ihren eindeutigen String Lobby Code identifiziert wird. Er wird
     * hierbei über seinen Authentifizierungstoken kenntlich gemacht. Kann der Nutzer der spezifizierten Lobby zugefügt
     * werden oder ist bereits darin, wird true zurück gegeben, sonst false.
     * Dies ist etwa der Fall wenn: <ul>
     * <li>Der angegeben Lobby Code keiner aktiven Lobby zugehörig ist</li>
     * <li>Die maximale Teilnehmerzahl der Lobby erreicht ist</li>
     * <li>Die Lobby sich bereits im Spiel befindet</li>
     * </ul>
     *
     * @param authToken the auth token
     * @param lobbyCode the identifier to specify the lobby
     * @return whether the user is part of the lobby afterwards
     * @throws IllegalArgumentException if the lobby code does not belong to any active lobby
     */
    boolean addToLobby(String authToken, String lobbyCode);

    /**
     * Der Nutzer Host wird über seine User ID identifiziert, der Zielnutzer, welcher entfernt werden soll über seinen
     * Nutzernamen. Hat der als Host anfragende Nutzer der Lobby, der er zugehörig ist sowie die benötigten Rechte um
     * einen anderen Spieler zu entfernen, wird das Ziel entfernt. Ist eine der Bedingungen nicht erfüllt oder das Ziel
     * nicht in der Lobby, geschieht nichts.
     *
     * @param invokerAuthToken the invoker auth token
     * @param targetUsername   the target username
     * @return whether the target was removed
     */
    boolean removeFromLobby(String invokerAuthToken, String targetUsername);

    /**
     * Ein Nutzer, identifiziert über seinen Authentifizierungstoken, wird aus der Lobby entfernt, in welcher er sich
     * aktuell befindet. Ist er in keiner Lobby, geschieht nichts.
     *
     * @param authToken the auth token
     * @return whether something changed
     */
    boolean removeFromLobby(String authToken);

    /**
     * Will set the host of the invokers lobby, where they should be host currently, to the target user. If the user
     * is not part of that lobby, false will be returned.
     *
     * @param invokerAuthToken the invoker auth token
     * @param targetUsername   the target username
     * @return whether the transfer was successful
     */
    boolean setLobbyHost(String invokerAuthToken, String targetUsername);

    /**
     * Setzt den Bereitschaftsstatus des spezifizierten Nutzers auf den mitgelieferten Wert sofern dieser einen
     * bekannten Zustand repräsentiert.
     * Capitalization and leading or tailing blanks will be ignored. Blanks in between will be replaced with "_" to
     * adjust to enum naming conventions.
     *
     * @param authToken the auth token
     * @param state     the state representation
     * @return whether the state was changed as a result
     */
    boolean setReadyState(String authToken, String state);

    /**
     * Gibt eine Liste an Lobby Stomps zurück, welche ausschließlich Lobbies mit Sichtbarkeit 'Öffentlich' enthält, die
     * sich nicht gerade in einer Aktivität befinden.
     *
     * @return the public lobbies
     */
    List<LobbyStomp> getPublicLobbies();

    /**
     * Will check if the user, specified by their auth token, is part of the lobby, identified by its unique lobby code.
     * When either the auth token is invalid, or the lobby code does not describe any managed lobby, true will be
     * returned.
     *
     * @param authToken the authentication token
     * @param lobbyCode the lobby code
     * @return whether the user is not part of the lobby
     */
    boolean isNotInLobby(String authToken, String lobbyCode);
}
