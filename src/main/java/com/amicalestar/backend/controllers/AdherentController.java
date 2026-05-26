package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.adherent.AdherentDTO;
import com.amicalestar.backend.dto.adherent.UpdateProfileRequest;
import com.amicalestar.backend.dto.election.AdherentLiteDTO;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.exceptions.ValidationException;
import com.amicalestar.backend.services.interfaces.AdherentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AdherentController {

    // Service de gestion des adhérents
    private final AdherentService adherentService;

    // === Récupération du profil utilisateur ===
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {

        // Email de l'utilisateur connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        AdherentDTO adherent = adherentService.getProfileDTOByEmail(email);

        Map<String, Object> response = new HashMap<>();

        response.put("nom", adherent.getNom());
        response.put("prenom", adherent.getPrenom());
        response.put("email", adherent.getEmail());
        response.put("telephone", adherent.getTelephone());
        response.put("matricule", adherent.getMatricule());
        response.put("cin", adherent.getCin());

        response.put("hasPhoto", adherent.isHasPhoto());
        response.put("photoUrl", adherent.getPhotoUrl());

        return ResponseEntity.ok(response);
    }

    // === Récupération de la photo de profil ===
    @GetMapping("/photo/{matricule}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String matricule) {

        // Recherche de l’adhérent
        Adherent adherent = adherentService.getByMatricule(matricule);

        if (adherent == null || adherent.getPhotoProfil() == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = adherent.getPhotoType();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        contentType != null ? contentType : MediaType.IMAGE_JPEG_VALUE
                )
                .body(adherent.getPhotoProfil());
    }

    // === Mise à jour du profil utilisateur ===
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @Valid @ModelAttribute UpdateProfileRequest request,
            BindingResult result
    ) {

        // Utilisateur connecté
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // Vérification des erreurs de validation
        if (result.hasErrors()) {

            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error ->
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    )
            );

            throw new ValidationException(errors);
        }

        // Mise à jour du profil
        adherentService.updateProfileByEmail(
                email,
                request
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Profil modifié avec succès"
                )
        );
    }

    // === Liste simplifiée des adhérents ===
    @GetMapping("/lite")
    public List<AdherentLiteDTO> getAllLite() {

        return adherentService.getAllLite();
    }

}