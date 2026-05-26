package com.amicalestar.backend.repositories.sondage;

import com.amicalestar.backend.entities.*;
import com.amicalestar.backend.entities.sondage.Participation;
import com.amicalestar.backend.entities.sondage.Sondage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // === Vérification de participation à un sondage ===
    boolean existsByAdherentAndSondage(
            Adherent adherent,
            Sondage sondage
    );

    // === Recherche de participation d’un adhérent ===
    Optional<Participation> findByAdherentAndSondage(
            Adherent adherent,
            Sondage sondage
    );

    // === Nombre de participations d’un sondage ===
    int countBySondageId(Long sondageId);

    // === Liste des participations d’un sondage ===
    List<Participation> findBySondageId(Long sondageId);

    // === Chargement complet des participations et réponses ===
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