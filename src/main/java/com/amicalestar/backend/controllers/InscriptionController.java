package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.*;
import com.amicalestar.backend.entities.Inscription;
import com.amicalestar.backend.services.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    // =========================
    // CREATE INSCRIPTION
    // =========================
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createInscription(
            @RequestPart("data") InscriptionRequest request,
            @RequestPart(value = "adherentFile", required = false) MultipartFile adherentFile,
            @RequestPart(value = "conjointFile", required = false) MultipartFile conjointFile,
            @RequestPart(value = "enfantsFiles", required = false) MultipartFile[] enfantsFiles
    ) {
        try {

            List<MultipartFile> enfantsList = null;
            if (enfantsFiles != null) {
                enfantsList = List.of(enfantsFiles);
            }

            inscriptionService.createInscription(
                    request,
                    adherentFile,
                    conjointFile,
                    enfantsList
            );

            return ResponseEntity.ok("Inscription créée avec succès ✅");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================
    // INSCRIPTION SIMPLE
    // =========================
    @PostMapping("/{matricule}/{eventId}")
    public ResponseEntity<Inscription> inscrire(
            @PathVariable String matricule,
            @PathVariable Long eventId) {

        Inscription inscription = inscriptionService.inscrire(matricule, eventId);
        return ResponseEntity.ok(inscription);
    }

    // =========================
    // MES INSCRIPTIONS (SECURISÉ)
    // =========================
    @GetMapping("/mes-inscriptions")
    public ResponseEntity<List<InscriptionDTO>> getMesInscriptions(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body(null);
        }

        String email = authentication.getName(); // 🔥 JWT = email

        List<InscriptionDTO> result = inscriptionService.getInscriptionsAdherent(email);

        return ResponseEntity.ok(result);
    }

    // =========================
    // DETAILS INSCRIPTION (SECURISÉ)
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            String email = authentication.getName();

            return ResponseEntity.ok(
                    inscriptionService.getByIdSecure(id, email)
            );

        } catch (Exception e) {

            // 🔥 AJOUTE ICI
            e.printStackTrace();

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/event/{id}")
    public ResponseEntity<List<InscriptionListDTO>> getByEvent(@PathVariable Long id) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsByEvent(id));
    }
    @GetMapping("/{id}/full")
    public ResponseEntity<InscriptionFullDTO> getFullDetails(@PathVariable Long id) {
        return ResponseEntity.ok(inscriptionService.getFullDetails(id));
    }
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut
    ) {
        try {
            inscriptionService.updateStatut(id, statut);
            return ResponseEntity.ok("Statut mis à jour");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}