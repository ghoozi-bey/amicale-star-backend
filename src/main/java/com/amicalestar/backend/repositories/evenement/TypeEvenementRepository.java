package com.amicalestar.backend.repositories.evenement;

import com.amicalestar.backend.entities.evenement.TypeEvenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeEvenementRepository extends JpaRepository<TypeEvenement, Long> {

    Optional<TypeEvenement> findByNom(String nom);
}