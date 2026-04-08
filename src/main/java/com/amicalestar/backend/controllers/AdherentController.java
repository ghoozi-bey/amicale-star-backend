package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.UpdateProfileRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.services.AdherentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AdherentController {

    private final AdherentService adherentService;

    // 🔥 GET PROFILE PRO
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Adherent adherent = adherentService.getProfileByEmail(email);

        Map<String, Object> response = new HashMap<>();

        response.put("nom", adherent.getNom());
        response.put("prenom", adherent.getPrenom());
        response.put("email", adherent.getEmail());
        response.put("telephone", adherent.getTelephone());

        // 🔥 CRITIQUE
        response.put("photoProfil", adherent.getPhotoProfil());

        return ResponseEntity.ok(response);
    }

    // 🔥 UPDATE PROFILE
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@ModelAttribute UpdateProfileRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            adherentService.updateProfileByEmail(email, request);
            return ResponseEntity.ok("Profil modifié avec succès");

        } catch (RuntimeException e) {

            System.err.println("Erreur update profile: " + e.getMessage());

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body("Erreur interne serveur");
        }
    }
}