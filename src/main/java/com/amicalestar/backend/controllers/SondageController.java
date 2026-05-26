package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.sondage.CreateSondageRequest;
import com.amicalestar.backend.dto.sondage.participation.ParticipationDTO;
import com.amicalestar.backend.dto.sondage.SondageResponse;
import com.amicalestar.backend.dto.sondage.stats.SondageStatsDTO;
import com.amicalestar.backend.entities.sondage.Sondage;
import com.amicalestar.backend.services.interfaces.SondageService;
import com.amicalestar.backend.services.interfaces.SondageStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sondages")
@RequiredArgsConstructor
public class SondageController {

    // Service de gestion des sondages
    private final SondageService sondageService;

    // Service des statistiques des sondages
    private final SondageStatsService statsService;

    // === Liste des sondages actifs ===
    @GetMapping("/actifs")
    public ResponseEntity<List<SondageResponse>> getAllPublic() {

        return ResponseEntity.ok(
                sondageService.getActiveSondages()
        );
    }

    // === Détails d’un sondage actif ===
    @GetMapping("/actifs/{id}")
    public ResponseEntity<SondageResponse> getPublicById(@PathVariable Long id) {

        return ResponseEntity.ok(
                sondageService.getActiveSondageById(id)
        );
    }

    // === Création d’un sondage ===
    @PostMapping
    public ResponseEntity<Sondage> createSondage(
            @RequestBody CreateSondageRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        Sondage sondage = sondageService.createSondage(
                request,
                email
        );

        return ResponseEntity.ok(sondage);
    }

    // === Détails d’un sondage ===
    @GetMapping("/{id}")
    public ResponseEntity<SondageResponse> getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                sondageService.getSondageById(id)
        );
    }

    // === Liste des sondages créés par l’utilisateur ===
    @GetMapping("/me")
    public ResponseEntity<List<SondageResponse>> getMySondages(Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                sondageService.getSondagesByCreatorEmail(email)
        );
    }

    // === Publication d’un sondage ===
    @PutMapping("/{id}/publish")
    public ResponseEntity<String> publish(@PathVariable Long id) {

        sondageService.publierSondage(id);

        return ResponseEntity.ok("Published");
    }

    // === Annulation de publication d’un sondage ===
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<String> unpublish(@PathVariable Long id) {

        sondageService.annulerPublication(id);

        return ResponseEntity.ok("Unpublished");
    }

    // === Rejet d’un sondage ===
    @PutMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable Long id) {

        sondageService.rejeterSondage(id);

        return ResponseEntity.ok("Rejected");
    }

    // === Mise à jour d’un sondage ===
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSondage(
            @PathVariable Long id,
            @RequestBody CreateSondageRequest request
    ) {

        sondageService.updateSondage(id, request);

        return ResponseEntity.ok().build();
    }

    // === Suppression d’un sondage ===
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        sondageService.supprimerSondage(id);

        return ResponseEntity.ok("Deleted");
    }

    // === Statistiques d’un sondage ===
    @GetMapping("/{id}/stats")
    public ResponseEntity<SondageStatsDTO> getStats(@PathVariable Long id) {

        return ResponseEntity.ok(
                statsService.getStats(id)
        );
    }

    // === Liste des participations d’un sondage ===
    @GetMapping("/{id}/participations")
    public ResponseEntity<List<ParticipationDTO>> getParticipations(@PathVariable Long id) {

        return ResponseEntity.ok(
                statsService.getParticipations(id)
        );
    }

}