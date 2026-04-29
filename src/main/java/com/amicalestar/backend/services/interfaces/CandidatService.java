package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.entities.election.Candidat;

import java.util.List;

public interface CandidatService {

    Candidat addCandidat(Long electionId, String adherentId);

    void removeCandidat(Long candidatId);

    List<Candidat> getElectionCandidats(Long electionId);

}
