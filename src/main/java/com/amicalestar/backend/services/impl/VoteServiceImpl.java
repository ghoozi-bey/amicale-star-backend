package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.election.VoteRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.election.Candidat;
import com.amicalestar.backend.entities.election.Election;
import com.amicalestar.backend.entities.election.Vote;
import com.amicalestar.backend.enums.StatutElection;
import com.amicalestar.backend.exceptions.ValidationException;
import com.amicalestar.backend.repositories.election.CandidatRepository;
import com.amicalestar.backend.repositories.election.ElectionRepository;
import com.amicalestar.backend.repositories.election.VoteRepository;
import com.amicalestar.backend.services.interfaces.VoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    private final ElectionRepository electionRepository;

    private final CandidatRepository candidatRepository;

    // === Vote utilisateur ===
    @Override
    @Transactional
    public void voter(
            VoteRequest request,
            Adherent currentUser
    ) {

        Election election = electionRepository
                .findById(request.getElectionId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Election introuvable"
                        ));

        // ===== VALIDATION ELECTION =====

        if (election.getStatut()
                != StatutElection.ACTIF) {

            throw new ValidationException(
                    "Election inactive"
            );
        }

        // ===== VALIDATION LISTE =====

        if (request.getCandidatIds() == null
                || request.getCandidatIds().isEmpty()) {

            throw new ValidationException(
                    "Aucun candidat sélectionné"
            );
        }

        // ===== VALIDATION MAX =====

        if (request.getCandidatIds().size()
                > election.getNombreGagnants()) {

            throw new ValidationException(
                    "Nombre maximum de votes dépassé"
            );
        }

        // ===== VALIDATION DOUBLONS =====

        Set<Long> uniqueIds =
                new HashSet<>(
                        request.getCandidatIds()
                );

        if (uniqueIds.size()
                != request.getCandidatIds().size()) {

            throw new ValidationException(
                    "Doublons détectés"
            );
        }

        boolean alreadyParticipated =
                voteRepository
                        .existsByElectionIdAndVoterMatricule(
                                election.getId(),
                                currentUser.getMatricule()
                        );

        // Vérification vote déjà effectué
        if (alreadyParticipated) {

            throw new ValidationException(
                    "Vous avez déjà voté"
            );
        }

        // ===== SAVE VOTES =====

        for (Long candidatId
                : request.getCandidatIds()) {

            Candidat candidat =
                    candidatRepository
                            .findById(candidatId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Candidat introuvable"
                                    ));

            // Vérification candidat appartient à l’élection
            if (!candidat.getElection()
                    .getId()
                    .equals(election.getId())) {

                throw new ValidationException(
                        "Candidat invalide"
                );
            }

            // Vérification auto vote
            if (candidat.getAdherent()
                    .getMatricule()
                    .equals(
                            currentUser.getMatricule()
                    )) {

                throw new ValidationException(
                        "Impossible de voter pour soi-même"
                );
            }

            Vote vote = new Vote();

            vote.setElection(election);

            vote.setVoter(currentUser);

            vote.setCandidat(candidat);

            LocalDateTime now =
                    LocalDateTime.now();

            vote.setVotedAt(now);

            voteRepository.save(vote);
        }
    }

    // === Vérification participation utilisateur ===
    @Override
    public boolean hasVoted(
            Long electionId,
            Adherent currentUser
    ) {

        return voteRepository
                .existsByElectionIdAndVoterMatricule(
                        electionId,
                        currentUser.getMatricule()
                );
    }

}