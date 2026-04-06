package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Evenement;
import java.util.List;

public interface EvenementService {

    Evenement createEvenement(Evenement evenement);

    List<Evenement> getAllEvenements();

    List<Evenement> getEvenementsCrees(String matricule);

    Evenement archiverEvenement(Long id);

    void deleteEvenement(Long id);

    Evenement updateEvenement(Long id, Evenement evenement);

    // 🔥 participation (ancien - tu peux garder ou supprimer après)
    List<Evenement> getMesEvenements(String matricule);

    // 🔥 NOUVEAU → événements où l’utilisateur est inscrit
    List<Evenement> getMesInscriptions(Long matricule);

    List<Evenement> getEvenementsActifs();
}