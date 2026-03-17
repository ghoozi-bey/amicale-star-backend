package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.dto.UpdateProfileRequest;
import com.amicalestar.backend.services.AdherentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/adherents")
@RequiredArgsConstructor
public class AdherentController {

    private final AdherentService adherentService;

    @DeleteMapping("/{matricule}")
    public void delete(@PathVariable String matricule) {
        adherentService.deleteAdherent(matricule);
    }

    @PatchMapping("/{matricule}")
    public Adherent update(@PathVariable String matricule, @RequestBody Adherent adherent) {
        return adherentService.updateAdherent(matricule, adherent);
    }

    @GetMapping("/test")
    public String test() {
        return "Adherent API fonctionne 🚀";
    }

    @PutMapping("/profile/{matricule}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String matricule,
            @RequestBody UpdateProfileRequest request
    ) {

        adherentService.updateProfile(matricule, request);

        return ResponseEntity.ok("Profil modifié");
    }
    @GetMapping("/profile/{matricule}")
    public ResponseEntity<Adherent> getProfile(@PathVariable String matricule) {

        Adherent adherent = adherentService.getProfile(matricule);

        return ResponseEntity.ok(adherent);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Adherent>> getAll() {
        return ResponseEntity.ok(adherentService.getAllAdherents());
    }
}