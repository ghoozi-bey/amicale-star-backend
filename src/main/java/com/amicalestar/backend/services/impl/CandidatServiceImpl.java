package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.election.Candidat;
import com.amicalestar.backend.entities.election.Election;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.Election.CandidatRepository;
import com.amicalestar.backend.repositories.Election.ElectionRepository;
import com.amicalestar.backend.services.interfaces.CandidatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatServiceImpl implements CandidatService {

    private final CandidatRepository candidatRepository;
    private final ElectionRepository electionRepository;
    private final AdherentRepository adherentRepository;

    @Override
    public Candidat addCandidat(Long electionId, String adherentId) {

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );

        Adherent adherent = adherentRepository.findById(adherentId)
                .orElseThrow(() ->
                        new RuntimeException("Adhérent introuvable")
                );

        boolean exists =
                candidatRepository.existsByAdherentIdAndElectionId(
                        adherentId,
                        electionId
                );

        if(exists) {
            throw new RuntimeException(
                    "Cet adhérent est déjà candidat"
            );
        }

        Candidat candidat = new Candidat();

        candidat.setElection(election);
        candidat.setAdherent(adherent);

        return candidatRepository.save(candidat);
    }

    @Override
    public void removeCandidat(Long candidatId) {

        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() ->
                        new RuntimeException("Candidat introuvable")
                );

        candidatRepository.delete(candidat);
    }

    @Override
    public List<Candidat> getElectionCandidats(Long electionId) {

        return candidatRepository.findByElectionId(electionId);
    }
}