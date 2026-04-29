package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.election.AddCandidatRequest;
import com.amicalestar.backend.dto.election.CandidatResponseDTO;
import com.amicalestar.backend.entities.election.Candidat;
import com.amicalestar.backend.services.interfaces.CandidatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CandidatController {

    private final CandidatService candidatService;

    // ADD CANDIDAT
    @PostMapping("/api/elections/{id}/candidats")
    public CandidatResponseDTO addCandidat(
            @PathVariable Long id,
            @RequestBody AddCandidatRequest request
    ) {

        return candidatService.addCandidat(id, request.getMatricule());
    }

    // GET CANDIDATS OF ELECTION
    @GetMapping("/api/elections/{id}/candidats")
    public List<CandidatResponseDTO> getElectionCandidats(
            @PathVariable Long id
    ) {

        return candidatService.getElectionCandidats(id);
    }

    // REMOVE CANDIDAT
    @DeleteMapping("/api/elections/{electionId}/candidats/{candidatId}")
    public void removeCandidat(
            @PathVariable Long electionId,
            @PathVariable Long candidatId
    ) {

        candidatService.removeCandidat(candidatId);
    }
}