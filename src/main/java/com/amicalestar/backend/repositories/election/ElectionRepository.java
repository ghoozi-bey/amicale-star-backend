package com.amicalestar.backend.repositories.election;

import com.amicalestar.backend.entities.election.Election;
import com.amicalestar.backend.enums.StatutElection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    List<Election> findByStatut(
            StatutElection statut
    );

}