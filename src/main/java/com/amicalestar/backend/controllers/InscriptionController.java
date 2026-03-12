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

    @PostMapping("/{matricule}/{eventId}")
    public Inscription inscrire(
            @PathVariable String matricule,
            @PathVariable Long eventId) {

        return inscriptionService.inscrire(matricule, eventId);
    }

    @GetMapping("/adherent/{matricule}")
    public List<Inscription> getMesEvenements(@PathVariable String matricule) {

        return inscriptionService.getInscriptionsAdherent(matricule);
    }
}