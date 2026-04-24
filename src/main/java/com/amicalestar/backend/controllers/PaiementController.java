package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Inscription;
import com.amicalestar.backend.entities.Paiement;
import com.amicalestar.backend.repositories.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "http://localhost:4200")
public class PaiementController {

    private final PaiementRepository paiementRepository;

    @GetMapping("/inscription/{id}")
    public List<Paiement> getPaiementsByInscription(@PathVariable Long id) {
        return paiementRepository.findByInscriptionId(id);
    }
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable Long id) {

        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        paiement.setStatut("PAYE");
        paiement.setDatePaiement(LocalDate.now());

        paiementRepository.save(paiement);

        return ResponseEntity.ok("Paiement validé ✅");
    }
    @PutMapping("/{id}/upload")
    public ResponseEntity<?> uploadJustificatif(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {

            Paiement paiement = paiementRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

            Inscription inscription = paiement.getInscription();

            // 🔒 Vérifier statut inscription
            if (inscription == null || !"ACCEPTEE".equalsIgnoreCase(inscription.getStatut())) {
                throw new RuntimeException("L'inscription doit être acceptée");
            }

            // 🔒 Vérifier mode paiement
            if (paiement.getModePaiement() == null ||
                    !paiement.getModePaiement().equalsIgnoreCase("VIREMENT")) {
                throw new RuntimeException("Upload autorisé uniquement pour VIREMENT");
            }

            // 🔒 Vérifier statut paiement
            if (!"EN_ATTENTE".equalsIgnoreCase(paiement.getStatut())) {
                throw new RuntimeException("Paiement déjà traité");
            }

            // 🔒 Vérifier fichier
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Fichier vide");
            }

            // 🔒 Vérifier type PDF
            if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
                throw new RuntimeException("Seuls les fichiers PDF sont autorisés");
            }

            // 🔥 IMPORTANT : IOException gérée ici
            paiement.setJustificatifVirement(file.getBytes());

            paiement.setStatut("EN_ATTENTE_VALIDATION");

            paiementRepository.save(paiement);

            return ResponseEntity.ok("Justificatif uploadé avec succès");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
