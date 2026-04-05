package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Sondage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SondageRepository extends JpaRepository<Sondage, Long> {
}