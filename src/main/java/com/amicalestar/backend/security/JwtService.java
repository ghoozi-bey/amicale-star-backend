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

        // ✅ ROLE
        claims.put("role", "ROLE_" + user.getTypeAdherent().name());

        // ✅ USERNAME (🔥 IMPORTANT)
        claims.put("username", user.getMatricule()); // ou getMatricule() si c’est ça chez toi

        // ✅ INFOS USER (optionnel)
        claims.put("prenom", user.getPrenom());
        claims.put("nom", user.getNom());

        // ✅ TYPE EVENEMENT
        if (user.getTypeEvenement() != null) {
            claims.put("type_evenement_id", user.getTypeEvenement().getId());
        } else {
            claims.put("type_evenement_id", null);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey())
                .compact();
    }
}