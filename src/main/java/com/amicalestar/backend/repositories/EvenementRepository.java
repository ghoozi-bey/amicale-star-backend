package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Evenement;
import com.amicalestar.backend.enums.StatutEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    // 🔵 créés
    List<Evenement> findByAdherent_Matricule(String matricule);

    // 🟢 dashboard
    List<Evenement> findByStatutNot(StatutEvenement statut);

    // 🔵 participation
    @Query("""
    SELECT e FROM Evenement e
    JOIN e.inscriptions i
    WHERE i.adherent.matricule = :matricule
    """)
    List<Evenement> findEventsWhereUserParticipates(@Param("matricule") String matricule);
    @Query("SELECT i.evenement FROM Inscription i WHERE i.adherent.matricule = :matricule")
    List<Evenement> findEvenementsByAdherentInscrit(@Param("matricule") Long matricule);
}