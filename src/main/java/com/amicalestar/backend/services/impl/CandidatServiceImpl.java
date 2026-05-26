package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.election.CandidatResponseDTO;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.election.Candidat;
import com.amicalestar.backend.entities.election.Election;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.election.CandidatRepository;
import com.amicalestar.backend.repositories.election.ElectionRepository;
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

    // Ajout candidat à une élection
    @Override
    public CandidatResponseDTO addCandidat(
            Long electionId,
            String matricule
    ) {

        // Recherche élection
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );

        // Recherche adhérent
        Adherent adherent = adherentRepository.findById(matricule)
                .orElseThrow(() ->
                        new RuntimeException("Adhérent introuvable")
                );

        // Vérifie si déjà candidat
        boolean exists =
                candidatRepository.existsByAdherentMatriculeAndElectionId(
                        matricule,
                        electionId
                );

        if(exists) {

            throw new RuntimeException(
                    "Cet adhérent est déjà candidat"
            );
        }

        // Création candidat
        Candidat candidat = new Candidat();

        candidat.setElection(election);
        candidat.setAdherent(adherent);

        Candidat saved = candidatRepository.save(candidat);

        // Construction DTO réponse
        CandidatResponseDTO dto = new CandidatResponseDTO();

        dto.setId(saved.getId());

        dto.setElectionId(
                saved.getElection().getId()
        );

        dto.setNom(
                saved.getAdherent().getNom()
        );

        dto.setPrenom(
                saved.getAdherent().getPrenom()
        );

        dto.setMatricule(
                saved.getAdherent().getMatricule()
        );

        return dto;
    }

    // Suppression candidat
    @Override
    public void removeCandidat(Long candidatId) {

        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() ->
                        new RuntimeException("Candidat introuvable")
                );

        candidatRepository.delete(candidat);
    }

    // Liste candidats d'une élection
    @Override
    public List<CandidatResponseDTO> getElectionCandidats(
            Long electionId
    ) {

        List<Candidat> candidats =
                candidatRepository.findByElectionId(electionId);

        return candidats.stream().map(candidat -> {

            // Construction DTO candidat
            CandidatResponseDTO dto =
                    new CandidatResponseDTO();

            dto.setId(candidat.getId());

            dto.setElectionId(
                    candidat.getElection().getId()
            );

            dto.setNom(
                    candidat.getAdherent().getNom()
            );

            dto.setPrenom(
                    candidat.getAdherent().getPrenom()
            );

            dto.setMatricule(
                    candidat.getAdherent().getMatricule()
            );

            return dto;

        }).toList();
    }
}