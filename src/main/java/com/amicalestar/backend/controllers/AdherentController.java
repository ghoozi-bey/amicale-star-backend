package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.UpdateProfileRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.services.AdherentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    // ================= GET PROFILE =================
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Adherent adherent = adherentService.getProfileByEmail(email);

        Map<String, Object> response = new HashMap<>();

        response.put("nom", adherent.getNom());
        response.put("prenom", adherent.getPrenom());
        response.put("email", adherent.getEmail());
        response.put("telephone", adherent.getTelephone());

        // 🔥 sécuriser si pas de photo
        if (adherent.getPhotoProfil() != null) {
            response.put("photoUrl", "http://localhost:8080/api/user/photo/" + adherent.getMatricule());
        } else {
            response.put("photoUrl", null);
        }

        return ResponseEntity.ok(response);
    }

    // ================= GET PHOTO =================
    @GetMapping("/photo/{matricule}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String matricule) {

        Adherent adherent = adherentService.getByMatricule(matricule);

        if (adherent == null || adherent.getPhotoProfil() == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = adherent.getPhotoType();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        contentType != null ? contentType : MediaType.IMAGE_JPEG_VALUE)
                .body(adherent.getPhotoProfil());
    }

    // ================= UPDATE PROFILE =================
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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