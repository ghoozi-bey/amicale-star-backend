package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Evenement;
import com.amicalestar.backend.entities.TypeEvenement;
import com.amicalestar.backend.repositories.EvenementRepository;
import com.amicalestar.backend.repositories.TypeEvenementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.amicalestar.backend.dto.EvenementDTO;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    private final EvenementRepository evenementRepository;
    private final TypeEvenementRepository typeRepo;

    // ✅ EVENEMENTS ACTIFS (SANS PHOTO LOURDE)
    @GetMapping("/actifs")
    public ResponseEntity<?> getEvenementsActifs() {

        List<EvenementDTO> list = evenementRepository.findAllLight()
                .stream()
                .map(obj -> {
                    EvenementDTO dto = new EvenementDTO();
                    dto.id = (Long) obj[0];
                    dto.titre = (String) obj[1];
                    dto.description = (String) obj[2];
                    dto.lieu = (String) obj[3];
                    dto.dateDebut = (java.time.LocalDate) obj[4];
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(list);
    }

    // ✅ MES EVENEMENTS
    @GetMapping("/mes-evenements-crees")
    public ResponseEntity<?> getMesEvenements() {
        List<Evenement> events = evenementRepository.findAll();

        // 🔥 SUPPRIMER PHOTO
        events.forEach(e -> e.setPhoto(null));

        return ResponseEntity.ok(events);
    }

    // ✅ GET PHOTO SEPARÉ (OPTIMISATION)
    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {

        Evenement e = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event introuvable"));

        if (e.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", e.getPhotoType())
                .body(e.getPhoto());
    }

    // ✅ CREATE EVENT (inchangé)
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