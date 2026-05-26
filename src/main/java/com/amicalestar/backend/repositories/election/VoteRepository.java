package com.amicalestar.backend.repositories.election;

import com.amicalestar.backend.entities.election.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoteRepository
        extends JpaRepository<Vote, Long> {

    // === Vérification d’un vote pour un candidat ===
    boolean existsByElectionIdAndVoterMatriculeAndCandidatId(
            Long electionId,
            String matricule,
            Long candidatId
    );

    // === Nombre de votes effectués par un adhérent ===
    long countByElectionIdAndVoterMatricule(
            Long electionId,
            String matricule
    );

    // === Nombre total de votes d’un candidat ===
    long countByCandidatId(Long candidatId);

    // === Vérification de participation à une élection ===
    boolean existsByElectionIdAndVoterMatricule(
            Long electionId,
            String matricule
    );

    // === Comptage des votes d’un candidat ===
    @Query("""
    SELECT COUNT(v)
    FROM Vote v
    WHERE v.candidat.id = :candidatId
    """)
    long countVotesByCandidatId(Long candidatId);
}