package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Evenement;
import java.util.List;

public interface EvenementService {

    Evenement createEvenement(Evenement evenement);

    List<Evenement> getAllEvenements();

    // 🔥 AJOUT IMPORTANT (pour image BLOB)
    Evenement getEvenementById(Long id);

    List<Evenement> getEvenementsCrees(String matricule);

    Evenement archiverEvenement(Long id);

    void deleteEvenement(Long id);

    Evenement updateEvenement(Long id, Evenement evenement);

    // 🔵 participation
    List<Evenement> getMesEvenements(String matricule);

    // 🟢 inscriptions
    List<Evenement> getMesInscriptions(Long matricule);

    // 🟡 dashboard
    List<Evenement> getEvenementsActifs();
}