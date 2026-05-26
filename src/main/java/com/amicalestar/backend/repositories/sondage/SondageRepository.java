package com.amicalestar.backend.repositories.sondage;

import com.amicalestar.backend.entities.sondage.Sondage;
import com.amicalestar.backend.enums.StatutSondage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SondageRepository extends JpaRepository<Sondage, Long> {

    // === Liste des sondages créés par un adhérent ===
    List<Sondage> findByCreatedBy_Matricule(String matricule);

    // === Liste des sondages par statut ===
    List<Sondage> findByStatut(StatutSondage statut);

    // === Chargement des sondages avec questions et créateur ===
    @Override
    @EntityGraph(attributePaths = {"questions", "createdBy"})
    List<Sondage> findAll();

    // === Liste des sondages créés par email ===
    @EntityGraph(attributePaths = {"questions", "createdBy"})
    List<Sondage> findByCreatedBy_Email(String email);

    // === Chargement détaillé d’un sondage ===
    @EntityGraph(attributePaths = {"questions", "createdBy"})
    Optional<Sondage> findDetailedById(Long id);

}