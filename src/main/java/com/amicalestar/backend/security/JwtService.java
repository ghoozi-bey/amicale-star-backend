package com.amicalestar.backend.security;

import com.amicalestar.backend.entities.Adherent;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String SECRET = "my-super-secret-key-12345678901234567890";

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(Adherent user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + user.getTypeAdherent().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey()) // FIX HERE
                .compact();
    }
}