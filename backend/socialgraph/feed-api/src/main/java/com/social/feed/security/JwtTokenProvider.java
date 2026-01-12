package com.social.feed.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        // Ensure your secret key in application.properties is at least 32 characters long
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String getUserIdFromToken(String token) {
        // UPDATE: Changed syntax for JJWT 0.12.3
        return Jwts.parser()
                .verifyWith(getSigningKey()) // setSigningKey -> verifyWith
                .build()
                .parseSignedClaims(token)    // parseClaimsJws -> parseSignedClaims
                .getPayload()                // getBody -> getPayload
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            // UPDATE: Changed syntax for JJWT 0.12.3
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}