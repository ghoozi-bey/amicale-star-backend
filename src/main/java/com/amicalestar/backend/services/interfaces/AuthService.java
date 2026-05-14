package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.AuthResponse;
import com.amicalestar.backend.dto.adherent.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

}