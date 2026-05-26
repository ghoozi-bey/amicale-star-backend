package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.election.CandidatResponseDTO;
import com.amicalestar.backend.entities.election.Candidat;

import java.util.List;

public interface CandidatService {

    // === Ajout d’un candidat à une élection ===
    CandidatResponseDTO addCandidat(
            Long electionId,
            String matricule
    );

    // === Suppression d’un candidat ===
    void removeCandidat(Long candidatId);

    // === Liste des candidats d’une élection ===
    List<CandidatResponseDTO> getElectionCandidats(Long electionId);

}