package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    List<Inscription> findByAdherentMatricule(String matricule);

}