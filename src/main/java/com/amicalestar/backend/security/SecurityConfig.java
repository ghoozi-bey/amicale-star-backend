package com.amicalestar.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    // === Configuration principale de Spring Security ===
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // Configuration CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Utilisation du mode stateless avec JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Authentification
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/inscriptions/**").authenticated()

                        // Autorisation des requêtes OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Gestion des événements
                        .requestMatchers(HttpMethod.GET, "/api/evenements/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/evenements/**").hasRole("MEMBRE_AMICALE")
                        .requestMatchers(HttpMethod.PUT, "/api/evenements/**").hasRole("MEMBRE_AMICALE")
                        .requestMatchers(HttpMethod.DELETE, "/api/evenements/**").hasRole("MEMBRE_AMICALE")

                        // Gestion des photos
                        .requestMatchers("/api/evenements/photo/**").permitAll()

                        // Gestion des sondages
                        .requestMatchers("/api/sondages/actifs/**").authenticated()
                        .requestMatchers("/api/sondages/**").hasRole("MEMBRE_AMICALE")

                        // Gestion des élections
                        .requestMatchers("/api/elections/actifs/**").authenticated()
                        .requestMatchers("/api/elections/**").hasRole("RESPONSABLE_ELECTION")

                        // Gestion utilisateur
                        .requestMatchers("/api/user/photo/**").permitAll()
                        .requestMatchers("/api/user/**").authenticated()

                        // Gestion administrateur
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Autorisation par défaut
                        .anyRequest().permitAll()
                )

                // Ajout du filtre JWT avant l’authentification Spring
                .addFilterBefore(
                        jwtFilter,
                        org.springframework.security.web.authentication
                                .UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // === Configuration CORS ===
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(
                List.of("http://localhost:4200")
        );

        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        config.setExposedHeaders(
                List.of("Authorization")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // === Encodeur des mots de passe ===
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // === Gestionnaire d’authentification Spring ===
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }
}