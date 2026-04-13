package com.amicalestar.backend.controllers;

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

    // ✅ ANCIEN (OK)
    @GetMapping("/adherent/{matricule}")
    public List<Inscription> getMesEvenements(@PathVariable String matricule) {
        return inscriptionService.getInscriptionsAdherent(matricule);
    }

    // ✅ 🔥 NOUVEAU (CORRIGÉ)
    @GetMapping("/mes-inscriptions/{matricule}")
    public List<?> getMesInscriptions(@PathVariable String matricule) {

        List<Inscription> inscriptions =
                inscriptionService.getInscriptionsAdherent(matricule);

        // 🔥 retourner seulement les événements
        return inscriptions.stream()
                .map(Inscription::getEvenement)
                .toList();
    }
}