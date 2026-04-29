package com.amicalestar.backend.repositories.Election;

import com.amicalestar.backend.entities.election.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {

    boolean existsByAdherentMatriculeAndElectionId(
            String matricule,
            Long electionId
    );

    List<Candidat> findByElectionId(Long electionId);

}
