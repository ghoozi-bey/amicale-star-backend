package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Evenement;
import com.amicalestar.backend.entities.TypeEvenement;
import com.amicalestar.backend.services.EvenementService;
import com.amicalestar.backend.services.TypeEvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
@CrossOrigin
public class EvenementController {

    private final EvenementService evenementService;
    private final TypeEvenementService typeEvenementService;

    @PostMapping(consumes = "multipart/form-data")
    public Evenement create(

            @RequestParam String titre,
            @RequestParam String lieu,
            @RequestParam String description,

            // 🔥 ON REÇOIT ID
            @RequestParam Long typeEvenement,

            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) Integer nbPlaces,
            @RequestParam(required = false) Double prix,

            @RequestParam(required = false) String societe,
            @RequestParam(required = false) String agence,
            @RequestParam(required = false) String destination,

            @RequestParam(required = false) MultipartFile photo
    ) {

        try {

            String fileName = null;

            // upload image
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

            Evenement e = new Evenement();

            e.setTitre(titre);
            e.setLieu(lieu);
            e.setDescription(description);
            e.setPhoto(fileName);

            // 🔥 récupérer type par ID
            TypeEvenement type = typeEvenementService.findById(typeEvenement);
            e.setTypeEvenement(type);

            String typeNom = type.getNom().toUpperCase().trim();

            System.out.println("TYPE ID = " + typeEvenement);
            System.out.println("TYPE NOM = " + typeNom);

            // 🔥 LOGIQUE MÉTIER CORRIGÉE
            switch (typeNom) {

                case "CONVENTION":
                    e.setSociete(societe);
                    break;

                case "OMRA & HAJ":
                    e.setAgence(agence);
                    if (nbPlaces != null) e.setNbPlaces(nbPlaces);
                    if (prix != null) e.setPrix(prix);
                    if (dateDebut != null) e.setDateDebut(LocalDate.parse(dateDebut));
                    if (dateFin != null) e.setDateFin(LocalDate.parse(dateFin));
                    break;

                case "VOYAGE":
                    e.setDestination(destination);
                    if (nbPlaces != null) e.setNbPlaces(nbPlaces);
                    if (prix != null) e.setPrix(prix);
                    if (dateDebut != null) e.setDateDebut(LocalDate.parse(dateDebut));
                    if (dateFin != null) e.setDateFin(LocalDate.parse(dateFin));
                    break;

                default:
                    throw new RuntimeException("Type invalide: " + typeNom);
            }

            return evenementService.createEvenement(e);

        } catch (Exception e) {
            throw new RuntimeException("Erreur création événement", e);
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