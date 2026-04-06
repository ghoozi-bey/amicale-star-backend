package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.UpdateProfileRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.services.AdherentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user") // 🔥 IMPORTANT
@RequiredArgsConstructor
public class AdherentController {

    private final AdherentService adherentService;

    @GetMapping("/profile")
    public ResponseEntity<Adherent> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(adherentService.getProfileByEmail(email));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        adherentService.updateProfileByEmail(email, request);
        return ResponseEntity.ok("Profil modifié");
    }
}