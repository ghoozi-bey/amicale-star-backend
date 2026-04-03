package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Evenement;

import java.util.List;

public interface EvenementService {

    Evenement createEvenement(Evenement evenement);

    List<Evenement> getAllEvenements();

    Evenement archiverEvenement(Long id);

    void deleteEvenement(Long id);

    Evenement updateEvenement(Long id, Evenement evenement);

    List<Evenement> getMesEvenements(String matricule);

    // 🔥 AJOUT IMPORTANT
    List<Evenement> getEvenementsActifs();
}