package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, String> {

    // === Recherche d’un adhérent par email ===
    Optional<Adherent> findByEmail(String email);

    // === Filtres des adhérents ===
    List<Adherent> findByDepartement(Departement departement);

    List<Adherent> findByActif(Boolean actif);

    // === Recherche des adhérents ===
    Optional<Adherent> findByCin(String cin);

    Optional<Adherent> findByMatricule(String matricule);

    List<Adherent> findByTypeAdherent(TypeAdherent typeAdherent);

    Optional<Adherent> findByTelephone(String telephone);

    // === Vérifications d’existence ===
    boolean existsByEmail(String email);

    boolean existsByCin(String cin);

    boolean existsByTelephone(String telephone);

    boolean existsByMatricule(String matricule);

    // === Vérification des doublons lors de modification ===
    boolean existsByEmailAndMatriculeNot(
            String email,
            String matricule
    );

    boolean existsByTelephoneAndMatriculeNot(
            String telephone,
            String matricule
    );

}