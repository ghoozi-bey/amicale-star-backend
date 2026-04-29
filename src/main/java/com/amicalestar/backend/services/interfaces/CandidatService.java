package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.election.CandidatResponseDTO;
import com.amicalestar.backend.entities.election.Candidat;

import java.util.List;

public interface CandidatService {

    CandidatResponseDTO addCandidat(Long electionId, String matricule);

    void removeCandidat(Long candidatId);

    List<CandidatResponseDTO> getElectionCandidats(Long electionId);

}
