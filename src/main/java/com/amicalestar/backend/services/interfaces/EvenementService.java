package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.entities.evenement.Evenement;
import java.util.List;

public interface EvenementService {

    // === Création d’un événement ===
    Evenement createEvenement(Evenement evenement);

    // === Liste de tous les événements ===
    List<Evenement> getAllEvenements();

    // === Recherche d’un événement par id ===
    Evenement getEvenementById(Long id);

    // === Liste des événements créés par un adhérent ===
    List<Evenement> getEvenementsCrees(String matricule);

    // === Archivage d’un événement ===
    Evenement archiverEvenement(Long id);

    // === Suppression d’un événement ===
    void deleteEvenement(Long id);

    // === Mise à jour d’un événement ===
    Evenement updateEvenement(
            Long id,
            Evenement evenement
    );

    // === Liste des événements participés ===
    List<Evenement> getMesEvenements(String matricule);

    // === Liste des inscriptions d’un adhérent ===
    List<Evenement> getMesInscriptions(Long matricule);

    // === Liste des événements actifs ===
    List<Evenement> getEvenementsActifs();
}