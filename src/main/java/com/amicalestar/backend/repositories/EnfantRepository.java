package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Enfant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnfantRepository extends JpaRepository<Enfant, Long> {

    List<Enfant> findByInscriptionId(Long inscriptionId);
}