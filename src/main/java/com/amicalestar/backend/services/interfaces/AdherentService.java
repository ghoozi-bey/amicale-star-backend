package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.dto.adherent.UpdateProfileRequest;
import com.amicalestar.backend.dto.adherent.AdherentDTO;


public interface AdherentService {

    // ================= READ =================
    Adherent getAdherentById(String matricule);

    // 🔥 utilisé pour image endpoint
    Adherent getByMatricule(String matricule);

    // ================= UPDATE ADMIN =================
    Adherent updateAdherent(String matricule, Adherent adherent);

    // ================= DELETE =================
    void deleteAdherent(String matricule);

    // ================= OLD PROFILE =================
    void updateProfile(String matricule, UpdateProfileRequest request);

    Adherent getProfile(String matricule);

    // ================= 🔥 NEW PRO VERSION =================

    /**
     * Profil via email (JWT)
     */
    Adherent getProfileByEmail(String email);

    /**
     * Update profil + image
     */
    void updateProfileByEmail(String email, UpdateProfileRequest request);
    AdherentDTO getProfileDTOByEmail(String email);
}