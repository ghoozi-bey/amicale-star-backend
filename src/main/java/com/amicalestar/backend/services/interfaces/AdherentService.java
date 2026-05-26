package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.election.AdherentLiteDTO;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.dto.adherent.UpdateProfileRequest;
import com.amicalestar.backend.dto.adherent.AdherentDTO;

import java.util.List;

public interface AdherentService {

    // === Recherche d’un adhérent par matricule ===
    Adherent getByMatricule(String matricule);

    // === Suppression d’un adhérent ===
    void deleteAdherent(String matricule);

    // === Mise à jour du profil utilisateur ===
    void updateProfile(
            String matricule,
            UpdateProfileRequest request
    );

    // === Récupération du profil utilisateur ===
    Adherent getProfile(String matricule);

    // === Récupération du profil via email JWT ===
    Adherent getProfileByEmail(String email);

    // === Mise à jour du profil via email JWT ===
    void updateProfileByEmail(
            String email,
            UpdateProfileRequest request
    );

    // === Récupération du profil sous forme DTO ===
    AdherentDTO getProfileDTOByEmail(String email);

    // === Liste simplifiée des adhérents ===
    List<AdherentLiteDTO> getAllLite();
}