package com.amicalestar.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Clé secrète utilisée pour signer le JWT
    private static final String SECRET = "my-super-secret-key-12345678901234567890";

    private final UserDetailsService userDetailsService;

    // === Génération de la clé de signature JWT ===
    private Key getSignInKey() {

        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // === Vérification et authentification du token JWT ===
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Ignore les requêtes OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {

            filterChain.doFilter(request, response);

            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // Vérification du header Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        final String jwt = authHeader.substring(7);

        try {

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String email = claims.getSubject();

            // Vérification de l’utilisateur authentifié
            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                // Création du token d’authentification Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Injection dans le contexte Spring Security
                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);

                System.out.println(
                        "✅ AUTH OK: " + userDetails.getAuthorities()
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "❌ JWT ERROR: " + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }

}