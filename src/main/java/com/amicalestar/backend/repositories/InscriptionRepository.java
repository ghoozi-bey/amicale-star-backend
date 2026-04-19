package com.amicalestar.backend.repositories;

import com.amicalestar.backend.dto.InscriptionDTO;
import com.amicalestar.backend.entities.Inscription;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

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
        WHERE a.matricule = :matricule
    """)
    List<InscriptionDTO> findDTOByMatricule(@Param("matricule") String matricule);


    // 🔥 FIX IMPORTANT ICI
    @Query("""
        SELECT DISTINCT i FROM Inscription i
        JOIN FETCH i.adherent
        LEFT JOIN FETCH i.conjoint
        LEFT JOIN FETCH i.enfants
        LEFT JOIN FETCH i.evenement
        WHERE i.id = :id
    """)
    Optional<Inscription> findByIdWithDetails(@Param("id") Long id);
}