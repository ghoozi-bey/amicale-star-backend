package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Evenement;
import com.amicalestar.backend.enums.StatutEvenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    // 🔥 événements du membre
    List<Evenement> findByAdherent_Matricule(String matricule);

    // 🔥 événements visibles pour adhérent (non archivés)
    List<Evenement> findByStatutNot(StatutEvenement statut);
}