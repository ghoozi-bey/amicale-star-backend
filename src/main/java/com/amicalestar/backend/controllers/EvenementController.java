package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.Evenement;
import com.amicalestar.backend.entities.TypeEvenement;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.EvenementRepository;
import com.amicalestar.backend.repositories.TypeEvenementRepository;
import com.amicalestar.backend.services.EvenementService;
import com.amicalestar.backend.dto.EvenementDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/evenements")
@CrossOrigin("*")
public class EvenementController {

    private final EvenementRepository evenementRepository;
    private final TypeEvenementRepository typeRepo;
    private final EvenementService evenementService;
    private final AdherentRepository adherentRepository;


    // 🔥 DTO MAPPING (CORRIGÉ)
    private EvenementDTO toDTO(Evenement e) {
        EvenementDTO dto = new EvenementDTO();

        dto.id = e.getId();
        dto.titre = e.getTitre();
        dto.description = e.getDescription();
        dto.lieu = e.getLieu();

        dto.dateDebut = e.getDateDebut();
        dto.dateFin = e.getDateFin();

        dto.prix = e.getPrix();
        dto.nbPlaces = e.getNbPlaces();

        dto.societe = e.getSociete();
        dto.agence = e.getAgence();
        dto.destination = e.getDestination();

        dto.photoUrl = "http://localhost:8080/api/evenements/photo/" + e.getId();

        // ✅ FIX PRINCIPAL
        dto.isInternational = e.getIsInternational();

        dto.typeEvenementId = e.getTypeEvenement() != null
                ? e.getTypeEvenement().getId()
                : null;

        return dto;
    }

    // 🌍 PUBLIC
    @GetMapping("/public")
    public ResponseEntity<?> getAllPublicEvents() {

        List<Object[]> events = evenementRepository.findAllLight();

        List<EvenementDTO> dtos = events.stream().map(obj -> {
            EvenementDTO dto = new EvenementDTO();

            dto.id = (Long) obj[0];
            dto.titre = (String) obj[1];
            dto.description = (String) obj[2];
            dto.lieu = (String) obj[3];
            dto.dateDebut = (LocalDate) obj[4];

            dto.photoUrl = "http://localhost:8080/api/evenements/photo/" + dto.id;

            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    // 🔥 DETAILS
    @GetMapping("/{id}")
    public ResponseEntity<?> getEvenementById(@PathVariable Long id) {

        Evenement e = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenement introuvable"));

        return ResponseEntity.ok(toDTO(e));
    }

    // ✅ ALL
    @GetMapping
    public ResponseEntity<?> getAllEvenements() {

        List<EvenementDTO> dtos = evenementRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // ✅ MES EVENEMENTS
    @GetMapping("/mes-evenements-crees")
    public ResponseEntity<?> getMesEvenements(@RequestParam String matricule) {

        List<EvenementDTO> dtos = evenementRepository
                .findByAdherent_Matricule(matricule)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // ✅ PLACES
    @GetMapping("/{id}/places")
    public ResponseEntity<Integer> getNbPlaces(@PathVariable Long id) {
        Evenement event = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));

        return ResponseEntity.ok(event.getNbPlaces());
    }

    // ✅ PHOTO
    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {

        byte[] photo = evenementRepository.getPhotoById(id);
        String type = evenementRepository.getPhotoTypeById(id);

        if (photo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", type)
                .body(photo);
    }

    // 🔥 CREATE (CORRIGÉ)
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
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "isInternational", required = false) Boolean isInternational

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

        // 🔥 TYPE
        TypeEvenement type = typeRepo.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Type introuvable"));

        e.setTypeEvenement(type);

        String typeNom = type.getNom().toUpperCase();

        if (typeNom.contains("OMRA") || typeNom.contains("HAJJ")) {
            e.setIsInternational(true);
        } else {
            e.setIsInternational(isInternational != null ? isInternational : false);
        }

        // 🔥 PHOTO
        if (photo != null && !photo.isEmpty()) {
            e.setPhoto(photo.getBytes());
            e.setPhotoType(photo.getContentType());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Adherent adherent = adherentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

        e.setAdherent(adherent);

        return ResponseEntity.ok(evenementService.createEvenement(e));
    }
}