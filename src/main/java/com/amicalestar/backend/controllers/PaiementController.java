package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.evenement.PaiementDTO;
import com.amicalestar.backend.entities.evenement.Inscription;
import com.amicalestar.backend.entities.evenement.Paiement;
import com.amicalestar.backend.repositories.evenement.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "http://localhost:4200")
public class PaiementController {

    private final PaiementRepository paiementRepository;

    @GetMapping("/inscription/{id}")
    public List<PaiementDTO> getPaiementsByInscription(@PathVariable Long id) {
        return paiementRepository.findDTOByInscriptionId(id);
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

    @Transactional
    @PutMapping("/{id}/upload")
    public ResponseEntity<?> uploadJustificatif(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {

            System.out.println("🔥 ===== DEBUG UPLOAD =====");

            System.out.println("ID = " + id);

            if (file == null) {
                System.out.println("❌ file = NULL");
            } else {
                System.out.println("📁 name = " + file.getOriginalFilename());
                System.out.println("📁 type = " + file.getContentType());
                System.out.println("📁 size = " + file.getSize());
            }

            Paiement paiement = paiementRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

            System.out.println("📌 statut paiement = " + paiement.getStatut());
            System.out.println("📌 mode paiement = " + paiement.getModePaiement());

            Inscription inscription = paiement.getInscription();

            if (inscription != null) {
                System.out.println("📌 statut inscription = " + inscription.getStatut());
            } else {
                System.out.println("❌ inscription NULL");
            }

            // ⚠️ TEMPORAIRE : on supprime toutes les conditions
            paiement.setJustificatifVirement(file.getBytes());
            paiement.setStatut("EN_ATTENTE_VALIDATION");

            paiementRepository.save(paiement);

            System.out.println("✅ SAVE OK");

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping("/{id}/justificatif")
    public ResponseEntity<byte[]> getJustificatif(@PathVariable Long id) {

        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        if (paiement.getJustificatifVirement() == null) {
            throw new RuntimeException("Aucun justificatif");
        }

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .body(paiement.getJustificatifVirement());
    }
    @PutMapping("/{id}/valider")
    public ResponseEntity<?> validerJustificatif(@PathVariable Long id) {

        Paiement p = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        p.setStatut("PAYE");
        p.setJustificatifValide(true);

        paiementRepository.save(p);

        return ResponseEntity.ok("Paiement validé ✅");
    }

    @PutMapping("/{id}/refuser")
    public ResponseEntity<?> refuserJustificatif(@PathVariable Long id) {

        Paiement p = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        p.setStatut("REFUSE");
        p.setJustificatifValide(false);

        paiementRepository.save(p);

        return ResponseEntity.ok("Paiement refusé ❌");
    }

}
