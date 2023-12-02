package com.pse.cardit.security.config;

import com.pse.cardit.user.model.IUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *  Definiert Methoden zum Lesen und Schreiben von JWT-Tokens.
 */
@Service
public class JwtService {
    private static final String SECRET_KEY = "CARDITMASTERPASSWORDAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final int JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * Extrahiert die Nutzer-Id aus dem Token.
     *
     * @param token the token
     * @return the long
     */
    public long extractUserId(String token) {
        //TODO: Dont.
        Logger logger = Logger.getLogger(this.getClass().getName());
        if (token.contains("Bearer")) {
            //TODO: I think this happens only if the method was called by a non security service
            logger.log(new LogRecord(Level.SEVERE, "Token was " + token));
        }
        String actualToken = token.replace("Bearer ", "");
        return Long.parseLong(extractClaim(actualToken, Claims::getSubject));
    }

    /**
     * Extrahiert die Claims (=enthaltenen Informationen).
     *
     * @param <T>            the type parameter
     * @param token          the token
     * @param claimsResolver the claims resolver
     * @return the t
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generiert das Token aus dem Nutzer.
     *
     * @param user the user
     * @return the string
     */
    public String generateToken(IUser user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * Generiert Token aus dem Nutzer und weiteren Informationen (z.B. Verfallsdatum).
     *
     * @param extraClaims the extra claims
     * @param user        the user
     * @return the string
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            IUser user
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(Long.toString(user.getUserId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Prüft, ob das Token gültig ist.
     *
     * @param token the token
     * @param user  the user
     * @return the boolean
     */
    public boolean isTokenValid(String token, IUser user) {
        final long userId = extractUserId(token);
        return (userId == user.getUserId()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
