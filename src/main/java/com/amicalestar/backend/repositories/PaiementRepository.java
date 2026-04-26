package com.amicalestar.backend.repositories;

import com.amicalestar.backend.dto.PaiementDTO;
import com.amicalestar.backend.entities.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    @Query("""
SELECT new com.amicalestar.backend.dto.PaiementDTO(
    p.id,
    p.montant,
    p.modePaiement,
    p.statut,
    p.datePaiement,
    CASE WHEN p.justificatifVirement IS NOT NULL THEN true ELSE false END
)
FROM Paiement p
WHERE p.inscription.id = :id
""")
    List<PaiementDTO> findDTOByInscriptionId(@Param("id") Long id);

}

