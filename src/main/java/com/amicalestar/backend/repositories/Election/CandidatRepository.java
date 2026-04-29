package com.amicalestar.backend.repositories.Election;

import com.amicalestar.backend.entities.election.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {

    boolean existsByAdherentIdAndElectionId(
            String adherentId,
            Long electionId
    );

    List<Candidat> findByElectionId(Long electionId);

}
