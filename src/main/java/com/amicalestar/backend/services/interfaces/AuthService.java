package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.AuthResponse;
import com.amicalestar.backend.dto.adherent.LoginRequest;

public interface AuthService {

    // === Authentification utilisateur ===
    AuthResponse login(LoginRequest request);

}