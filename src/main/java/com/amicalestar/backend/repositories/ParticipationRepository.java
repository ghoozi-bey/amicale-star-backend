package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    boolean existsByAdherentAndSondage(Adherent adherent, Sondage sondage);
}