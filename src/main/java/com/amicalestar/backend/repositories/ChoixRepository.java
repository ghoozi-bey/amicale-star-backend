package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Choix;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoixRepository extends JpaRepository<Choix, Long> {
}