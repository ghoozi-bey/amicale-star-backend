package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Evenement;
import com.amicalestar.backend.services.EvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
@CrossOrigin
public class EvenementController {

    private final EvenementService evenementService;

    @PostMapping(consumes = "multipart/form-data")
    public Evenement create(
            @RequestParam String titre,
            @RequestParam String lieu,
            @RequestParam String description,
            @RequestParam String dateDebut,
            @RequestParam String dateFin,
            @RequestParam Integer nbPlaces,
            @RequestParam Double prix,
            @RequestParam(required = false) MultipartFile photo
    ) {

        try {

            String fileName = null;

            if (photo != null && !photo.isEmpty()) {

                fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();

                Path uploadPath = Paths.get("uploads/events/");
                Files.createDirectories(uploadPath);

                Files.copy(
                        photo.getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            Evenement evenement = new Evenement();
            evenement.setTitre(titre);
            evenement.setLieu(lieu);
            evenement.setDescription(description);
            evenement.setDateDebut(java.time.LocalDate.parse(dateDebut));
            evenement.setDateFin(java.time.LocalDate.parse(dateFin));
            evenement.setNbPlaces(nbPlaces);
            evenement.setPrix(prix);
            evenement.setPhoto(fileName);

            return evenementService.createEvenement(evenement);

        } catch (Exception e) {
            throw new RuntimeException("Erreur upload image", e);
        }
    }

    @GetMapping
    public List<Evenement> getAll() {
        return evenementService.getAllEvenements();
    }

    @PatchMapping("/{id}/archiver")
    public Evenement archiver(@PathVariable Long id) {
        return evenementService.archiverEvenement(id);
    }

    @GetMapping("/test")
    public String test() {
        return "Backend Amicale STAR fonctionne 🚀";
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
    }

    @PatchMapping("/{id}")
    public Evenement updatePrix(@PathVariable Long id, @RequestBody Evenement evenement) {
        return evenementService.updateEvenement(id, evenement);
    }
}