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

    @PostMapping
    public ResponseEntity<Adherent> create(@RequestBody Adherent adherent) {
        return ResponseEntity.ok(adherentService.createAdherent(adherent));
    }

    @DeleteMapping("/{matricule}")
    public ResponseEntity<Void> delete(@PathVariable String matricule) {
        adherentService.deleteAdherent(matricule);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{matricule}")
    public ResponseEntity<Adherent> update(
            @PathVariable String matricule,
            @RequestBody Adherent adherent
    ) {
        return ResponseEntity.ok(adherentService.updateAdherent(matricule, adherent));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Adherent API fonctionne 🚀");
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
        return ResponseEntity.ok(adherentService.getProfile(matricule));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Adherent>> getAll() {
        return ResponseEntity.ok(adherentService.getAllAdherents());
    }


}