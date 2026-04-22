package com.amicalestar.backend.repositories;

import com.amicalestar.backend.dto.InscriptionDTO;
import com.amicalestar.backend.dto.InscriptionListDTO;
import com.amicalestar.backend.entities.Inscription;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    // =========================
    // 🔥 LISTE PAR EMAIL
    // =========================
    @Query("""
        SELECT new com.amicalestar.backend.dto.InscriptionDTO(
            i.id,
            i.statut,
            i.modePaiement,
            i.statutPaiement,
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
SELECT new com.amicalestar.backend.dto.InscriptionListDTO(
    i.id,
    a.nom,
    a.email,
    i.modePaiement,
    i.statut
)
FROM Inscription i
JOIN i.adherent a
WHERE i.evenement.id = :eventId
""")
    List<InscriptionListDTO> findDTOByEventId(@Param("eventId") Long eventId);

}
