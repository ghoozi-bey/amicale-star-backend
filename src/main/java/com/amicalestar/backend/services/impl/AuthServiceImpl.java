package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.AuthResponse;
import com.amicalestar.backend.dto.adherent.LoginRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.security.JwtService;
import com.amicalestar.backend.services.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdherentRepository adherentRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest request) {

        try {

            // EMAIL REQUIRED
            if (request.getEmail() == null
                    || request.getEmail().trim().isEmpty()) {

                return new AuthResponse(
                        "EMAIL_REQUIRED",
                        null
                );
            }

            // PASSWORD REQUIRED
            if (request.getPassword() == null
                    || request.getPassword().trim().isEmpty()) {

                return new AuthResponse(
                        "PASSWORD_REQUIRED",
                        null
                );
            }

            // FIND USER
            Adherent adherent = adherentRepository
                    .findByEmail(request.getEmail().trim())
                    .orElse(null);

            // EMAIL NOT FOUND
            if (adherent == null) {

                return new AuthResponse(
                        "EMAIL_NOT_FOUND",
                        null
                );
            }

            // ACCOUNT DISABLED
            if (adherent.getActif() == null
                    || !adherent.getActif()) {

                return new AuthResponse(
                        "ACCOUNT_DISABLED",
                        null
                );
            }

            // PASSWORD CHECK
            boolean matches = passwordEncoder.matches(
                    request.getPassword(),
                    adherent.getPassword()
            );

            if (!matches) {

                return new AuthResponse(
                        "INVALID_PASSWORD",
                        null
                );
            }

            // TOKEN
            String token =
                    jwtService.generateToken(adherent);

            return new AuthResponse(
                    null,
                    token
            );

        }

        catch (Exception e) {

            e.printStackTrace();

            return new AuthResponse(
                    "SERVER_ERROR",
                    null
            );

        }

    }

}