package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByInscriptionId(Long inscriptionId);

}

