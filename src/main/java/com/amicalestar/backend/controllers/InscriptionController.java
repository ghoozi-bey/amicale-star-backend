package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.InscriptionDTO;
import com.amicalestar.backend.dto.InscriptionRequest;
import com.amicalestar.backend.entities.Inscription;
import com.amicalestar.backend.services.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    // ✅ INSCRIPTION SIMPLE (JSON ONLY)
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody InscriptionRequest request) {

        inscriptionService.createInscription(request, null, null);

        return ResponseEntity.ok("Inscription créée avec succès ✅");
    }

    // ✅ ANCIEN ENDPOINT (optionnel)
    @PostMapping("/{matricule}/{eventId}")
    public ResponseEntity<Inscription> inscrire(
            @PathVariable String matricule,
            @PathVariable Long eventId) {

        Inscription inscription = inscriptionService.inscrire(matricule, eventId);
        return ResponseEntity.ok(inscription);
    }

    // ✅ MES INSCRIPTIONS
    @GetMapping("/mes-inscriptions/{matricule}")
    public ResponseEntity<List<InscriptionDTO>> getMesInscriptions(@PathVariable String matricule) {

        List<Inscription> inscriptions =
                inscriptionService.getInscriptionsAdherent(matricule);

        List<InscriptionDTO> result = inscriptions.stream()
                .map(i -> InscriptionDTO.builder()
                        .statut(i.getStatut())
                        .modePaiement(i.getModePaiement())
                        .statutPaiement(i.getStatutPaiement())
                        .evenement(i.getEvenement())
                        .build())
                .toList();

        return ResponseEntity.ok(result);
    }
}