package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.AuthResponse;
import com.amicalestar.backend.dto.adherent.LoginRequest;
import com.amicalestar.backend.services.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    // Service d'authentification
    private final AuthService authService;

    // === Authentification de l'utilisateur ===
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        return authService.login(request);
    }

}