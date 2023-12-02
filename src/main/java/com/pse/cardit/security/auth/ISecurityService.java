package com.pse.cardit.security.auth;

/**
 * Verarbeitet die HTTP-Anfragen zur Registrierung und Login, indem die vom Nutzer gesendeten Daten verarbeitet und
 * daraus Token erstellt werden. Anschließend wird das Token in den Body der Response geschrieben.
 */
public interface ISecurityService {
    /**
     * Registriert den Nutzer in der Datenbank und generiert das Token.
     *
     * @param request the request
     * @return the authentication response
     */
    AuthenticationResponse register(RegisterRequest request);

    /**
     * Überprüft die Korrektheit der Nutzerdaten und generiert das Token.
     *
     * @param request the request
     * @return the authentication response
     */
    AuthenticationResponse login(LoginRequest request);
}
