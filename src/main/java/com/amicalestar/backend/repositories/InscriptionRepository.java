package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Inscription;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    @Query("SELECT i FROM Inscription i JOIN FETCH i.evenement WHERE i.adherent.matricule = :matricule")
    List<Inscription> findByAdherentMatricule(@Param("matricule") String matricule);
}