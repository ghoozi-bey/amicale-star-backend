package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.ParticipationRequest;
import com.amicalestar.backend.dto.ParticipationResponse;
import com.amicalestar.backend.services.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping("/participate")
    public ResponseEntity<?> participate(
            @RequestBody ParticipationRequest request,
            Principal principal) {

        participationService.submitParticipation(request, principal.getName());

        return ResponseEntity.ok(Map.of("message", "Participation saved"));
    }

    @GetMapping("/me/{sondageId}")
    public ResponseEntity<ParticipationResponse> getMyParticipation(
            @PathVariable Long sondageId,
            Principal principal
    ) {
        return ResponseEntity.ok(
                participationService.getUserParticipation(sondageId, principal.getName())
        );
    }
}