package com.amicalestar.backend.repositories.sondage;

import com.amicalestar.backend.entities.sondage.Choix;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoixRepository extends JpaRepository<Choix, Long> {
}