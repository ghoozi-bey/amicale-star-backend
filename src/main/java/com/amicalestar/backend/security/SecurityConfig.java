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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // 🔥 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // 🔐 AUTH
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/inscriptions/**").authenticated()

                        // 🔁 OPTIONS (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // EVENEMENTS
                        .requestMatchers(HttpMethod.GET, "/api/evenements/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/evenements/**").hasRole("MEMBRE_AMICALE")
                        .requestMatchers(HttpMethod.PUT, "/api/evenements/**").hasRole("MEMBRE_AMICALE")
                        .requestMatchers(HttpMethod.DELETE, "/api/evenements/**").hasRole("MEMBRE_AMICALE")

                        // PHOTO
                        .requestMatchers("/api/evenements/photo/**").permitAll()

                        // SONDAGES
                        .requestMatchers("/api/sondages/public/**").authenticated()
                        .requestMatchers("/api/sondages/**").hasRole("MEMBRE_AMICALE")

                        // ELECTIONS
                        .requestMatchers("/api/elections/public/**").authenticated()
                        .requestMatchers("/api/elections/**").hasRole("RESPONSABLE_ELECTION")

                        // USER
                        .requestMatchers("/api/user/photo/**").permitAll()
                        .requestMatchers("/api/user/**").authenticated()

                        // ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // DEFAULT
                        .anyRequest().permitAll()
                )

                // ✅ FILTRE JWT CORRECT
                .addFilterBefore(jwtFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // 🔥 CORS CONFIG
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}