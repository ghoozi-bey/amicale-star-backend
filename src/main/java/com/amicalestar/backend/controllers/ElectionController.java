package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.election.AdherentLiteDTO;
import com.amicalestar.backend.dto.election.AttribuerRoleDTO;
import com.amicalestar.backend.dto.election.CreateElectionRequest;
import com.amicalestar.backend.dto.election.ElectionResponseDTO;
import com.amicalestar.backend.entities.election.Election;
import com.amicalestar.backend.entities.evenement.TypeEvenement;
import com.amicalestar.backend.services.interfaces.ElectionService;
import com.amicalestar.backend.services.interfaces.TypeEvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    // Service de gestion des élections
    private final ElectionService electionService;

    // Service de gestion des types d’événements
    private final TypeEvenementService typeEvenementService;

    // === Liste des élections actives ===
    @GetMapping("/actifs")
    public ResponseEntity<List<ElectionResponseDTO>> getAllPublic() {

        return ResponseEntity.ok(
                electionService.getActiveElections()
        );
    }

    // === Détails d’une élection active ===
    @GetMapping("/actifs/{id}")
    public ResponseEntity<ElectionResponseDTO> getPublicById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                electionService.getActiveElectionById(id)
        );
    }

    // === Liste des types d’événements ===
    @GetMapping("/types-evenements")
    public List<TypeEvenement> getTypeEvenements() {

        return typeEvenementService.getAll();
    }

    // === Création d’une élection ===
    @PostMapping
    public ElectionResponseDTO createElection(
            @RequestBody CreateElectionRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return electionService.create(request, email);
    }

    // === Liste de toutes les élections ===
    @GetMapping
    public List<ElectionResponseDTO> getAllElections() {

        return electionService.getAll();
    }

    // === Détails d’une élection ===
    @GetMapping("/{id}")
    public ElectionResponseDTO getElectionById(
            @PathVariable Long id
    ) {

        return electionService.getById(id);
    }

    // === Mise à jour d’une élection ===
    @PutMapping("/{id}")
    public ElectionResponseDTO updateElection(
            @PathVariable Long id,
            @RequestBody CreateElectionRequest request
    ) {

        return electionService.update(id, request);
    }

    // === Suppression d’une élection ===
    @DeleteMapping("/{id}")
    public void deleteElection(
            @PathVariable Long id
    ) {

        electionService.delete(id);
    }

    // === Publication d’une élection ===
    @PutMapping("/{id}/publish")
    public void publish(
            @PathVariable Long id
    ) {

        electionService.publish(id);
    }

    // === Annulation de publication d’une élection ===
    @PutMapping("/{id}/unpublish")
    public void unpublish(
            @PathVariable Long id
    ) {

        electionService.unpublish(id);
    }

    // === Rejet d’une élection ===
    @PutMapping("/{id}/reject")
    public void reject(
            @PathVariable Long id
    ) {

        electionService.reject(id);
    }

    // === Liste des adhérents éligibles ===
    @GetMapping("/{id}/eligible-adherents")
    public List<AdherentLiteDTO> getEligibleAdherents(
            @PathVariable Long id
    ) {

        return electionService.getEligibleAdherents(id);
    }

    // === Statistiques d’une élection ===
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getStats(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                electionService.getStats(id)
        );
    }

    // === Attribution des rôles aux gagnants ===
    @PostMapping("/{id}/attribuer-roles")
    public ResponseEntity<?> attribuerRoles(

            @PathVariable Long id,

            @RequestBody
            List<AttribuerRoleDTO> request
    ) {

        electionService.attribuerRoles(
                id,
                request
        );

        return ResponseEntity.ok().build();
    }

    // === Liste des gagnants d’une élection ===
    @GetMapping("/{id}/winners")
    public ResponseEntity<?> getWinners(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                electionService.getElectionWinners(id)
        );
    }

}