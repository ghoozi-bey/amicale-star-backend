package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.sondage.participation.ParticipationRequest;
import com.amicalestar.backend.dto.sondage.participation.ParticipationResponse;
import com.amicalestar.backend.services.interfaces.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor
public class ParticipationController {

    // Service de gestion des participations
    private final ParticipationService participationService;

    // === Enregistrement d’une participation ===
    @PostMapping("/participate")
    public ResponseEntity<?> participate(
            @RequestBody ParticipationRequest request,
            Principal principal
    ) {

        participationService.submitParticipation(
                request,
                principal.getName()
        );

        return ResponseEntity.ok(
                Map.of("message", "Participation saved")
        );
    }

    // === Récupération de la participation de l’utilisateur ===
    @GetMapping("/me/{sondageId}")
    public ResponseEntity<ParticipationResponse> getMyParticipation(
            @PathVariable Long sondageId,
            Principal principal
    ) {

        return ResponseEntity.ok(
                participationService.getUserParticipation(
                        sondageId,
                        principal.getName()
                )
        );
    }
}