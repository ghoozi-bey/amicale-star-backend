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

    // Clé secrète utilisée pour signer le JWT
    private final String SECRET = "my-super-secret-key-12345678901234567890";

    // === Génération de la clé de signature JWT ===
    private Key getSignInKey() {

        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // === Génération du token JWT utilisateur ===
    public String generateToken(Adherent user) {

        Map<String, Object> claims = new HashMap<>();

        // Ajout du rôle utilisateur
        claims.put("role", user.getTypeAdherent().name());

        // Ajout du matricule utilisateur
        claims.put("username", user.getMatricule());

        // Ajout des informations utilisateur
        claims.put("prenom", user.getPrenom());
        claims.put("nom", user.getNom());

        // Ajout du type d’événement associé
        if (user.getTypeEvenement() != null) {

            claims.put(
                    "type_evenement_id",
                    user.getTypeEvenement().getId()
            );

        } else {

            claims.put("type_evenement_id", null);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60 * 24
                        )
                )
                .signWith(getSignInKey())
                .compact();
    }
}