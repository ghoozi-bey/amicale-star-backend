package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Sondage;
import com.amicalestar.backend.enums.StatutSondage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SondageRepository extends JpaRepository<Sondage, Long> {
    List<Sondage> findByStatut(StatutSondage statut);
}