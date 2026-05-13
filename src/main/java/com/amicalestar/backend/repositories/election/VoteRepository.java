package com.amicalestar.backend.repositories.election;

import com.amicalestar.backend.entities.election.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoteRepository
        extends JpaRepository<Vote, Long> {

    boolean existsByElectionIdAndVoterMatriculeAndCandidatId(
            Long electionId,
            String matricule,
            Long candidatId
    );

    long countByElectionIdAndVoterMatricule(
            Long electionId,
            String matricule
    );

    long countByCandidatId(Long candidatId);

    boolean existsByElectionIdAndVoterMatricule(
            Long electionId,
            String matricule
    );

    @Query("""
    SELECT COUNT(v)
    FROM Vote v
    WHERE v.candidat.id = :candidatId
    """)
    long countVotesByCandidatId(Long candidatId);
}