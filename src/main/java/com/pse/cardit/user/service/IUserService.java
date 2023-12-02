package com.pse.cardit.user.service;

import com.pse.cardit.security.auth.RegisterRequest;
import com.pse.cardit.user.model.User;
import com.pse.cardit.user.service.controll.DeleteRequest;
import com.pse.cardit.user.service.request.ChangeCardSkinRequest;
import com.pse.cardit.user.service.request.ChangeDiceSkinRequest;
import com.pse.cardit.user.service.request.ChangePasswordRequest;
import com.pse.cardit.user.service.request.ChangeProfilePictureRequest;
import com.pse.cardit.user.service.request.ChangeUsernameRequest;
import com.pse.cardit.user.service.response.ChangeCardSkinResponse;
import com.pse.cardit.user.service.response.ChangeDiceSkinResponse;
import com.pse.cardit.user.service.response.ChangePasswordResponse;
import com.pse.cardit.user.service.response.ChangeProfilePictureResponse;
import com.pse.cardit.user.service.response.ChangeUsernameResponse;

/**
 * Eine Schnittstelle, welche die bereitgestellte Funktionalität der Komponente definiert. Andere Komponenten müssen mit
 * dieser Schnittstelle interagieren, wenn sie Dienste der User-Komponente benötigen.
 */
public interface IUserService {
    /**
     * Gibt den User mit der übergebenen userId zurück
     *
     * @param userId the user id
     * @return the user
     *
     * @throws IllegalArgumentException if the id does not belong to a user
     */
    User getUser(long userId);

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    User getUser(String username);

    /**
     * Gets user from token.
     *
     * @param authToken the auth token
     * @return the user from token
     */
    User getUserFromToken(String authToken);

    /**
     * Ein neuer Registered User wird in der Datenbank registriert.
     *
     * @param request the request
     * @return the user
     */
    User addUser(RegisterRequest request);

    /**
     * Ein neuer Guest User wird in der Datenbank registriert.
     *
     * @param username the username
     * @return the user
     */
    User addUser(String username);

    /**
     * A new bot user is registered. He must be deleted once he is no longer used to not clog the database.
     * @param botName the bots name
     * @return the user
     */
    User addBot(String botName);

    /**
     * Der Nutzer in dem DeleteRequest wird aus der Datenbank gelöscht.
     *
     * @param request the request
     */
    void deleteUser(DeleteRequest request);

    void deleteUser(User user);

    /**
     * Das Profilbild des Users der aus dem authToken extrahiert wird, wird zu den Customisable geändert, das der
     * pictureId entspricht.
     *
     * @param request   the request
     * @param authToken the auth token
     * @return the change profile picture response
     */
    ChangeProfilePictureResponse changeProfilePicture(ChangeProfilePictureRequest request, String authToken);

    /**
     * Der Username des Users mit der userId die im Request mitgeschickt wird, wird auf newUsername geändert
     *
     * @param request the request
     */
    void changeUsername(ChangeUsernameRequest request, String authToken);

    /**
     * Das Passwort des Users der aus dem authToken extrahiert wird, wird auf newPassword geändert
     *
     * @param request   the request
     * @param authToken the auth token
     */
    void changePassword(ChangePasswordRequest request, String authToken);

    /**
     * Der Würfel-Skin den der User ausgewählt hat wird geändert
     *
     * @param request   the request
     * @param authToken the auth token
     * @return the change dice skin response
     */
    ChangeDiceSkinResponse changeDiceSkin(ChangeDiceSkinRequest request, String authToken);

    /**
     * Der Karten-Skin den der User ausgewählt hat wird geändert
     *
     * @param request   the request
     * @param authToken the auth token
     * @return the change card skin response
     */
    ChangeCardSkinResponse changeCardSkin(ChangeCardSkinRequest request, String authToken);
}
