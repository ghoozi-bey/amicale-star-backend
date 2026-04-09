package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Evenement;
import com.amicalestar.backend.entities.TypeEvenement;
import com.amicalestar.backend.repositories.EvenementRepository;
import com.amicalestar.backend.repositories.TypeEvenementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    private final EvenementRepository evenementRepository;
    private final TypeEvenementRepository typeRepo; // 🔥 AJOUT ICI

    // ✅ CREATE EVENT (MULTIPART FIX)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createEvenement(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam(value = "lieu", required = false) String lieu,
            @RequestParam(value = "dateDebut", required = false) String dateDebut,
            @RequestParam(value = "dateFin", required = false) String dateFin,
            @RequestParam(value = "prix", required = false) Double prix,
            @RequestParam(value = "nbPlaces", required = false) Integer nbPlaces,
            @RequestParam(value = "societe", required = false) String societe,
            @RequestParam(value = "agence", required = false) String agence,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam("typeEvenement") Long typeId,
            @RequestParam(value = "photo", required = false) MultipartFile photo
    ) throws Exception {

        Evenement e = new Evenement();

        e.setTitre(titre);
        e.setDescription(description);
        e.setLieu(lieu);

        if (dateDebut != null && !dateDebut.isEmpty())
            e.setDateDebut(LocalDate.parse(dateDebut));

        if (dateFin != null && !dateFin.isEmpty())
            e.setDateFin(LocalDate.parse(dateFin));

        e.setPrix(prix);
        e.setNbPlaces(nbPlaces);

        e.setSociete(societe);
        e.setAgence(agence);
        e.setDestination(destination);

        // 🔥 FIX PHOTO (IMPORTANT)
        if (photo != null && !photo.isEmpty()) {
            e.setPhoto(photo.getBytes());
            e.setPhotoType(photo.getContentType());
        }

        TypeEvenement type = typeRepo.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Type introuvable"));

        e.setTypeEvenement(type);

        return ResponseEntity.ok(evenementRepository.save(e));
    }
}