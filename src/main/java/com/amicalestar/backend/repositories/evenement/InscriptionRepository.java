package com.amicalestar.backend.repositories.evenement;

import com.amicalestar.backend.dto.evenement.EnfantDTO;
import com.amicalestar.backend.dto.evenement.InscriptionDTO;
import com.amicalestar.backend.dto.evenement.InscriptionListDTO;
import com.amicalestar.backend.entities.evenement.Inscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    boolean existsByAdherentMatriculeAndEvenementId(String matricule, Long evenementId);
    Page<Inscription> findByEvenementId(Long eventId, Pageable pageable);
    // =========================
    // 🔥 LISTE PAR EMAIL
    // =========================
    @Query("""
        SELECT new com.amicalestar.backend.dto.evenement.InscriptionDTO(
            i.id,
            i.statut,
            e.id,
            e.titre
        )
        FROM Inscription i
        JOIN i.evenement e
        JOIN i.adherent a
        WHERE a.email = :email
    """)
    List<InscriptionDTO> findDTOByEmail(@Param("email") String email);

    // =========================
    // 🔥 DETAILS
    // =========================
    @Query("""
        SELECT DISTINCT i FROM Inscription i
        JOIN FETCH i.adherent
        LEFT JOIN FETCH i.conjoint
        LEFT JOIN FETCH i.enfants
        LEFT JOIN FETCH i.evenement
        WHERE i.id = :id
    """)
    Optional<Inscription> findByIdWithDetails(@Param("id") Long id);
    @Query("""
SELECT new com.amicalestar.backend.dto.evenement.InscriptionListDTO(
    i.id,
    a.nom,
    a.email,

    /* mode paiement (1 seule ligne) */
    (
        SELECT p.modePaiement FROM Paiement p
        WHERE p.inscription.id = i.id
        AND p.id = (
            SELECT MIN(p2.id)
            FROM Paiement p2
            WHERE p2.inscription.id = i.id
        )
    ),

    /* statut paiement (1 seule ligne) */
    (
        SELECT p.statut FROM Paiement p
        WHERE p.inscription.id = i.id
        AND p.id = (
            SELECT MIN(p2.id)
            FROM Paiement p2
            WHERE p2.inscription.id = i.id
        )
    ),

    i.statut
)
FROM Inscription i
JOIN i.adherent a
WHERE i.evenement.id = :eventId
""")
    Page<InscriptionListDTO> findDTOByEventId(
            @Param("eventId") Long eventId,
            Pageable pageable
    );

    @Query("SELECT new com.amicalestar.backend.dto.evenement.EnfantDTO(e.nom, e.prenom, e.dateNaissance) " +
            "FROM Enfant e WHERE e.inscription.id = :id")
    List<EnfantDTO> findEnfantsDTOByInscriptionId(@Param("id") Long id);


}
