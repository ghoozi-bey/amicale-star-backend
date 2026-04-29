package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.election.Candidat;
import com.amicalestar.backend.services.interfaces.CandidatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CandidatController {

    private final CandidatService candidatService;

    // ADD CANDIDAT
    @PostMapping("/api/elections/{id}/candidats")
    public Candidat addCandidat(
            @PathVariable Long id,
            @RequestParam String adherentId
    ) {

        return candidatService.addCandidat(id, adherentId);
    }

    // GET CANDIDATS OF ELECTION
    @GetMapping("/api/elections/{id}/candidats")
    public List<Candidat> getElectionCandidats(
            @PathVariable Long id
    ) {

        return candidatService.getElectionCandidats(id);
    }

    // REMOVE CANDIDAT
    @DeleteMapping("/api/candidats/{id}")
    public void removeCandidat(
            @PathVariable Long id
    ) {

        candidatService.removeCandidat(id);
    }
}