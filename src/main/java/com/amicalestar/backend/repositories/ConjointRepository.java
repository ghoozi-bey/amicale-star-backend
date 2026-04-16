package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Conjoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConjointRepository extends JpaRepository<Conjoint, Long> {

    Optional<Conjoint> findByInscriptionId(Long inscriptionId);
}