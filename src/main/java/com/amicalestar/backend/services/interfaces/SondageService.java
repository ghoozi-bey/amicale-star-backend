package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.sondage.CreateSondageRequest;
import com.amicalestar.backend.dto.sondage.SondageResponse;
import com.amicalestar.backend.entities.sondage.Sondage;

import java.util.List;

public interface SondageService {

    // === Création d’un sondage ===
    Sondage createSondage(
            CreateSondageRequest request,
            String matricule
    );

    // === Liste de tous les sondages ===
    List<SondageResponse> getAllSondages();

    // === Liste des sondages créés par utilisateur ===
    List<SondageResponse> getSondagesByCreatorEmail(String email);

    // === Recherche d’un sondage par id ===
    SondageResponse getSondageById(Long id);

    // === Publication d’un sondage ===
    Sondage publierSondage(Long id);

    // === Mise à jour automatique du statut ===
    void updateStatut(Sondage s);

    // === Annulation de publication ===
    Sondage annulerPublication(Long id);

    // === Rejet d’un sondage ===
    void rejeterSondage(Long id);

    // === Mise à jour d’un sondage ===
    Sondage updateSondage(
            Long id,
            CreateSondageRequest request
    );

    // === Suppression d’un sondage ===
    void supprimerSondage(Long id);

    // === Liste des sondages actifs ===
    List<SondageResponse> getActiveSondages();

    // === Recherche d’un sondage actif ===
    SondageResponse getActiveSondageById(Long id);
}