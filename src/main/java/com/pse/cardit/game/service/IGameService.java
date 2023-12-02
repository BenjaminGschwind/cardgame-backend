package com.pse.cardit.game.service;

import com.pse.cardit.game.service.controll.GameChannelResponse;
import com.pse.cardit.game.service.controll.GameStateResponse;
import com.pse.cardit.game.service.controll.InteractResponse;

import java.util.Map;

/**
 * Das Interface IGameService stellt die verschiedenen Spiel-Funktionalitäten nach außen hin bereit. Es hält Methoden um
 * {@link com.pse.cardit.game.model.AGame} Instanzen zu verwalten und mit diesen zu interagieren.
 * Es bildet eine Facade und erwartet als Übergabeparameter lediglich primitive Datentypen, Strings, sowie den festen
 * Enum {@link GameType} um die verschiedenen Spiele zu repräsentieren.
 * Die Entscheidung, diesen zu verwenden anstelle eines Strings ist gefallen, da an vielen Stellen geprüft werden muss,
 * um welchen Spiel Typ es sich handelt. Daher steht der Enum ohnehin auch nach außen zur Verfügung und kann hier direkt
 * verwendet werden. Für Spieler hingegen werden userID's verwendet anstelle von konkreten
 * {@link com.pse.cardit.user.model.IUser} Objekten. Somit muss eine aufrufende Instanz, die das Interface ansteuert,
 * sich nicht selbst um eine Kommunikation mit dem {@link com.pse.cardit.user.service.IUserService} kümmern.
 */
public interface IGameService {
    /**
     * Diese Methode fragt den Zustand eines Spiels, identifiziert über seine gameID, ab. Wie dieser Zustand aussieht,
     * ist maßgeblich vom Spiel Typ abhängig. Daher wird er als String verpackt, welcher ein JSON Objekt repräsentiert.
     * Aus diesem Format können alle Informationen gelesen werden, welche zum Anzeigen eines Spiels wichtig sind.
     *
     * @param gameId the game id
     * @return the state
     */
    String getState(String gameId);

    /**
     * Setzt eine codierte Interaktion eines Spielers, identifiziert über seine userID, um. Gibt es für den Nutzer eine
     * codierte Reaktion darauf, erhält er diese im Rückgabewert als String. Sonst erhält er einen leeren String zurück.
     * Entspricht die Codierung der Interaktion keiner validen Aktion im Spiel des Nutzers, so wird ein aussagekräftiger
     * Fehler geworfen.
     *
     * @param authToken   the auth token
     * @param interaction the interaction
     * @return the string
     */
    InteractResponse interact(String authToken, String interaction);

    /**
     * Will check if the next player is controlled by an actual user, or should be handled by service itself. If it
     * is a bot, an interaction will be determined and executed.
     *
     * @param gameId the game identifier
     * @return whether some action was performed
     */
    boolean botInteraction(String gameId);

    /**
     * Erzeugt eine neue Spielinstanz, welche vom Service verwaltet wird mit einem Spiel Typ, teilnehmenden User ID's
     * sowie einer, womöglich leeren, Liste an codierten Sonderregeln. Gibt die Game ID des Spiels zurück. Es kann
     * fehlschlagen, ein Spiel erstellen zu lassen, wenn etwa:
     * <li>
     *     <item>Mindestens einer der Nutzer bereits in einem Spiel ist</item>
     *     <item>Eine der Sonderregelungen keinem spezifischen gültigen Format entspricht</item>
     *     <item>Das Limit der gleichzeitig verwalteten Instanzen erreicht wurde</item>
     *     <item>Die Menge der Nutzer nicht zu den unterstützen Spielerzahlen des Spiel Typs passt</item>
     * </li>
     *
     * @param userIds  the user ids
     * @param gameType the game type
     * @param rules    the rules
     * @return the string
     */
    String createGame(long[] userIds, GameType gameType, Map<String, String> rules);

    /**
     * Löscht die verwaltete Spielinstanz, identifiziert über ihre Game ID. Existiert die Instanz nicht, geschieht
     * nichts
     *
     * @param gameId the game id
     */
    void deleteGame(String gameId);


    /**
     * Gets personal game state. Will attempt to get the user from their auth token. If they are currently in any
     * game, that games state will be returned.
     *
     * @param token the authorization json web token
     * @return the game state of the users game
     * @throws IllegalArgumentException if the user is not in any game
     */
    GameStateResponse getPersonalState(String token);

    /**
     * Will check if the user, identified by their auth token, is currently in the game specified by its unique
     * identifier.
     *
     * @param authToken      the auth token
     * @param gameIdentifier the game identifier
     * @return whether the user is currently in the game
     */
    boolean isNotInGame(String authToken, String gameIdentifier);

    /**
     * Gets personal channel.
     *
     * @param token the token
     * @return the personal channel
     */
    GameChannelResponse getPersonalChannel(String token);
}
