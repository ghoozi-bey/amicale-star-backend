package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.TypeEvenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeEvenementRepository extends JpaRepository<TypeEvenement, Long> {

    Optional<TypeEvenement> findByNom(String nom);
}