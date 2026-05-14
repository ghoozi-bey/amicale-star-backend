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

    private final ElectionService electionService;
    private final TypeEvenementService typeEvenementService;

    // Endpoints for all users
    @GetMapping("/actifs")
    public ResponseEntity<List<ElectionResponseDTO>> getAllPublic() {

        return ResponseEntity.ok(
                electionService.getActiveElections()
        );
    }

    @GetMapping("/actifs/{id}")
    public ResponseEntity<ElectionResponseDTO> getPublicById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                electionService.getActiveElectionById(id)
        );
    }

    //Endpoints for responsable election

    //Load types evenements
    @GetMapping("/types-evenements")
    public List<TypeEvenement> getTypeEvenements() {

        return typeEvenementService.getAll();
    }

    // CREATE
    @PostMapping
    public ElectionResponseDTO createElection(
            @RequestBody CreateElectionRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return electionService.create(request, email);
    }

    // GET ALL
    @GetMapping
    public List<ElectionResponseDTO> getAllElections() {

        return electionService.getAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ElectionResponseDTO getElectionById(
            @PathVariable Long id
    ) {

        return electionService.getById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ElectionResponseDTO updateElection(
            @PathVariable Long id,
            @RequestBody CreateElectionRequest request
    ) {

        return electionService.update(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteElection(
            @PathVariable Long id
    ) {
        electionService.delete(id);
    }

    // PUBLISH
    @PutMapping("/{id}/publish")
    public void publish(
            @PathVariable Long id
    ) {

        electionService.publish(id);
    }

    // UNPUBLISH
    @PutMapping("/{id}/unpublish")
    public void unpublish(
            @PathVariable Long id
    ) {

        electionService.unpublish(id);
    }

    // REJECT
    @PutMapping("/{id}/reject")
    public void reject(
            @PathVariable Long id
    ) {

        electionService.reject(id);
    }

    @GetMapping("/{id}/eligible-adherents")
    public List<AdherentLiteDTO> getEligibleAdherents(
            @PathVariable Long id
    ) {

        return electionService
                .getEligibleAdherents(id);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getStats(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                electionService.getStats(id)
        );
    }

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

    @GetMapping("/{id}/winners")
    public ResponseEntity<?> getWinners(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                electionService
                        .getElectionWinners(id)
        );
    }

}