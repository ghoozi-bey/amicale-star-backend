package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Candidat;

import java.util.List;

public interface CandidatService {

    Candidat addCandidat(Long electionId, String adherentId);

    void removeCandidat(Long candidatId);

    List<Candidat> getElectionCandidats(Long electionId);

}
