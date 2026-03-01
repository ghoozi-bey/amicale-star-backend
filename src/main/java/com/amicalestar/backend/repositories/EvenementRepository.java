package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
}