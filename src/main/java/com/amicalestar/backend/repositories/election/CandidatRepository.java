package com.amicalestar.backend.repositories.election;

import com.amicalestar.backend.entities.election.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {

    // === Vérification de candidature dans une élection ===
    boolean existsByAdherentMatriculeAndElectionId(
            String matricule,
            Long electionId
    );

    // === Liste des candidats d’une élection ===
    List<Candidat> findByElectionId(Long electionId);

}