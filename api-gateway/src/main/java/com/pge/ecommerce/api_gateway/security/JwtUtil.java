package com.pge.ecommerce.api_gateway.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {


    @Value("${spring.jwt.secret}")
    private String secret;


    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }


    public boolean isValid(String token) {
        try { extractEmail(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }
}