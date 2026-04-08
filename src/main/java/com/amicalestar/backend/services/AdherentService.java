package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.dto.UpdateProfileRequest;

import java.util.List;

public interface AdherentService {

    // ================= CREATE =================
    Adherent createAdherent(Adherent adherent);

    // ================= READ =================
    List<Adherent> getAllAdherents();

    Adherent getAdherentById(String matricule);

    // ================= UPDATE ADMIN =================
    Adherent updateAdherent(String matricule, Adherent adherent);

    // ================= DELETE =================
    void deleteAdherent(String matricule);

    // ================= OLD PROFILE (on garde pour compatibilité) =================
    void updateProfile(String matricule, UpdateProfileRequest request);

    Adherent getProfile(String matricule);

    // ================= 🔥 NEW PRO VERSION (SECURISEE) =================

    /**
     * Récupérer profil utilisateur connecté via email (JWT)
     */
    Adherent getProfileByEmail(String email);

    /**
     * Mettre à jour profil avec :
     * - nom / prénom
     * - email / téléphone
     * - changement mot de passe sécurisé (avec vérification)
     */
    void updateProfileByEmail(String email, UpdateProfileRequest request);
}