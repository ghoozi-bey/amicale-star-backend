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

    private final AdherentService adherentService;

    // ================= GET PROFILE =================
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {

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
    public ResponseEntity<?> updateProfile(
            @Valid @ModelAttribute UpdateProfileRequest request,
            BindingResult result
    ) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // ================= VALIDATION =================

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

        // ================= UPDATE =================

        adherentService.updateProfileByEmail(
                email,
                request
        );

        // ================= RESPONSE =================

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Profil modifié avec succès"
                )
        );
    }

    @GetMapping("/lite")
    public List<AdherentLiteDTO> getAllLite() {

        return adherentService.getAllLite();
    }

}