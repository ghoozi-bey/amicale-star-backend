package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.Departement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, String> {

    // 🔐 utilisé pour login (UserDetailsService)
    Optional<Adherent> findByEmail(String email);

    List<Adherent> findByDepartement(Departement departement);

    List<Adherent> findByActif(Boolean actif);

    Optional<Adherent> findByCin(String cin);
}