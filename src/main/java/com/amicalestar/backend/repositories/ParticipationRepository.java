package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    boolean existsByAdherentAndSondage(Adherent adherent, Sondage sondage);
    Optional<Participation> findByAdherentAndSondage(Adherent adherent, Sondage sondage);
    int countBySondageId(Long sondageId);
    List<Participation> findBySondageId(Long sondageId);
    @Query("""
        SELECT DISTINCT p
        FROM Participation p
        LEFT JOIN FETCH p.adherent
        LEFT JOIN FETCH p.reponses r
        LEFT JOIN FETCH r.question
        LEFT JOIN FETCH r.choix
        WHERE p.sondage.id = :sondageId
    """)
    List<Participation> findWithDetailsBySondageId(Long sondageId);
}