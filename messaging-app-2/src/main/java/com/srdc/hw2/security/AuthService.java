package com.srdc.hw2.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

/**
 * AuthService provides methods for handling authentication tokens.
 */
public class AuthService {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a random secret key
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    /**
     * Generates a JWT token for a user.
     *
     * @param username the username of the user
     * @param isAdmin whether the user is an admin
     * @return the generated JWT token
     */
    public static String login(String username, boolean isAdmin) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim("isAdmin", isAdmin)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Checks if a user is an admin based on the JWT token.
     *
     * @param token the JWT token to check
     * @return true if the user is an admin, false otherwise
     */
    public static boolean isAdmin(String token) {
        try {
            return (Boolean) Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("isAdmin");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retrieves the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username if present, null otherwise
     */
    public static String getUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
