package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.Sondage;
import com.amicalestar.backend.enums.StatutSondage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SondageRepository extends JpaRepository<Sondage, Long> {
    List<Sondage> findByCreatedBy_Matricule(String matricule);
    List<Sondage> findByStatut(StatutSondage statut);
    @Override
    @EntityGraph(attributePaths = {"questions", "createdBy"})
    List<Sondage> findAll();

    @EntityGraph(attributePaths = {"questions", "createdBy"})
    List<Sondage> findByCreatedBy_Email(String email);

    @EntityGraph(attributePaths = {"questions", "createdBy"})
    Optional<Sondage> findDetailedById(Long id);
}