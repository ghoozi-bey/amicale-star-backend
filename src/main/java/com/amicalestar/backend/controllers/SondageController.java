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

    private final SondageService sondageService;
    private final SondageStatsService statsService;

    // Endpoints for all users
    @GetMapping("/actifs")
    public ResponseEntity<List<SondageResponse>> getAllPublic() {
        return ResponseEntity.ok(sondageService.getActiveSondages());
    }

    @GetMapping("/actifs/{id}")
    public ResponseEntity<SondageResponse> getPublicById(@PathVariable Long id) {
        return ResponseEntity.ok(sondageService.getActiveSondageById(id));
    }

    // Endpoints for membres amicale
    @PostMapping
    public ResponseEntity<Sondage> createSondage( @RequestBody CreateSondageRequest request, Authentication authentication ) {

        String email = authentication.getName();

        Sondage sondage = sondageService.createSondage(request, email);

        return ResponseEntity.ok(sondage);

    }

    @GetMapping("/{id}")
    public ResponseEntity<SondageResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sondageService.getSondageById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<SondageResponse>> getMySondages(Authentication authentication) {
        String email = authentication.getName();

        return ResponseEntity.ok(sondageService.getSondagesByCreatorEmail(email));
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<String> publish(@PathVariable Long id) {
        sondageService.publierSondage(id);
        return ResponseEntity.ok("Published");
    }

    @PutMapping("/{id}/unpublish")
    public ResponseEntity<String> unpublish(@PathVariable Long id) {
        sondageService.annulerPublication(id);
        return ResponseEntity.ok("Unpublished");
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        sondageService.rejeterSondage(id);
        return ResponseEntity.ok("Rejected");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSondage(
            @PathVariable Long id,
            @RequestBody CreateSondageRequest request
    ) {
        sondageService.updateSondage(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        sondageService.supprimerSondage(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<SondageStatsDTO> getStats(@PathVariable Long id) {
        return ResponseEntity.ok(statsService.getStats(id));
    }

    @GetMapping("/{id}/participations")
    public ResponseEntity<List<ParticipationDTO>> getParticipations(@PathVariable Long id) {
        return ResponseEntity.ok(statsService.getParticipations(id));
    }

}