package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.InscriptionDTO;
import com.amicalestar.backend.dto.InscriptionDetailsDTO;
import com.amicalestar.backend.dto.InscriptionRequest;
import com.amicalestar.backend.entities.Inscription;
import com.amicalestar.backend.services.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InscriptionController {

    private final InscriptionService inscriptionService;

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

            return ResponseEntity.ok().body("Inscription créée avec succès ✅");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/{matricule}/{eventId}")
    public ResponseEntity<Inscription> inscrire(
            @PathVariable String matricule,
            @PathVariable Long eventId) {

        Inscription inscription = inscriptionService.inscrire(matricule, eventId);
        return ResponseEntity.ok(inscription);
    }

    @GetMapping("/mes-inscriptions/{matricule}")
    public ResponseEntity<List<InscriptionDTO>> getMesInscriptions(@PathVariable String matricule) {

        List<InscriptionDTO> result = inscriptionService.getInscriptionsAdherent(matricule);

        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        try {
            System.out.println("ID reçu = " + id);

            return ResponseEntity.ok(inscriptionService.getById(id));

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 IMPORTANT
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}