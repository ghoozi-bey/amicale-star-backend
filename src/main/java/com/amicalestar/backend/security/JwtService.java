package com.amicalestar.backend.security;

import com.amicalestar.backend.entities.Adherent;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String SECRET = "secret-key";

    public String generateToken(Adherent user) {

        Map<String, Object> claims = new HashMap<>();

        // 🔥 CORRECTION ICI
        claims.put("role", "ROLE_" + user.getTypeAdherent().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }
}