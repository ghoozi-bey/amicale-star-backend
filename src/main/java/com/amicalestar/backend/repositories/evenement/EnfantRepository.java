package com.amicalestar.backend.repositories.evenement;

import com.amicalestar.backend.entities.evenement.Enfant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnfantRepository extends JpaRepository<Enfant, Long> {

    // === Liste des enfants liés à une inscription ===
    List<Enfant> findByInscriptionId(Long inscriptionId);
}