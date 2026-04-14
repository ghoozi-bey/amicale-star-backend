package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.InscriptionDTO;
import com.amicalestar.backend.entities.Inscription;
import com.amicalestar.backend.services.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;

    // ✅ INSCRIPTION
    @PostMapping("/{matricule}/{eventId}")
    public Inscription inscrire(
            @PathVariable String matricule,
            @PathVariable Long eventId) {

        return inscriptionService.inscrire(matricule, eventId);
    }

    // ✅ 🔥 VERSION PRO (DTO)
    @GetMapping("/mes-inscriptions/{matricule}")
    public List<InscriptionDTO> getMesInscriptions(@PathVariable String matricule) {

        List<Inscription> inscriptions =
                inscriptionService.getInscriptionsAdherent(matricule);

        return inscriptions.stream()
                .map(i -> InscriptionDTO.builder()
                        .statut(i.getStatut())
                        .evenement(i.getEvenement())
                        .build())
                .toList();
    }
}