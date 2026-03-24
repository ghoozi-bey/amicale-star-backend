package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.LoginRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdherentRepository adherentRepository;

    @PostMapping("/login")
    public Adherent login(@RequestBody Adherent loginData){

        Adherent adherent = adherentRepository
                .findByEmailAndPassword(
                        loginData.getEmail(),
                        loginData.getPassword()
                );

        if(adherent == null){
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        return adherent;
    }

    /*@PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return jwtService.generateToken(request.getEmail());
    }*/
}