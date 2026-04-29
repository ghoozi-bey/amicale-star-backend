package com.amicalestar.backend.repositories.evenement;

import com.amicalestar.backend.entities.evenement.Evenement;
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
    @Query("SELECT e.id, e.titre, e.description, e.lieu, e.dateDebut, e.statut FROM Evenement e")
    List<Object[]> findAllLight();
    @Query("SELECT e.photo FROM Evenement e WHERE e.id = :id")
    byte[] getPhotoById(@Param("id") Long id);

    @Query("SELECT e.photoType FROM Evenement e WHERE e.id = :id")
    String getPhotoTypeById(@Param("id") Long id);
    @Query("SELECT COUNT(i) FROM Inscription i WHERE i.evenement.id = :eventId")
    int countInscriptions(@Param("eventId") Long eventId);
    @Query("SELECT e FROM Evenement e WHERE " +
            "(:budget IS NULL OR e.prix <= :budget) AND " +
            "(:participants IS NULL OR e.nbPlaces >= :participants) AND " +
            "LOWER(e.typeEvenement.nom) LIKE LOWER(CONCAT('%', :type, '%'))")
    List<Evenement> findRecommended(
            @Param("budget") Integer budget,
            @Param("participants") Integer participants,
            @Param("type") String type
    );
}