package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AdherentRepository adherentRepository;

    @PostMapping("/login")
    public Adherent login(@RequestBody Adherent loginData){

        Adherent adherent = adherentRepository
                .findByEmailAndMotdepasse(
                        loginData.getEmail(),
                        loginData.getMotdepasse()
                );

        if(adherent == null){
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        return adherent;
    }

}