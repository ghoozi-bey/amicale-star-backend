package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.dto.AdherentDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, String> {

    // ================= AUTH =================
    Optional<Adherent> findByEmail(String email);

    // ================= FILTRES =================
    List<Adherent> findByDepartement(Departement departement);
    List<Adherent> findByActif(Boolean actif);

    // ================= RECHERCHE =================
    Optional<Adherent> findByCin(String cin);
    Optional<Adherent> findByMatricule(String matricule);

    // ================= VALIDATIONS =================
    boolean existsByEmail(String email);
    boolean existsByCin(String cin);
    boolean existsByTelephone(String telephone);
    boolean existsByMatricule(String matricule);

    boolean existsByEmailAndMatriculeNot(String email, String matricule);
    boolean existsByTelephoneAndMatriculeNot(String telephone, String matricule);

}