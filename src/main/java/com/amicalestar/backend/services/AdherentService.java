package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.dto.UpdateProfileRequest;

import java.util.List;

public interface AdherentService {

    // 🔵 EXISTANT (on garde pour compatibilité)
    Adherent createAdherent(Adherent adherent);

    List<Adherent> getAllAdherents();

    Adherent getAdherentById(String matricule);

    Adherent updateAdherent(String matricule, Adherent adherent);

    void deleteAdherent(String matricule);

    void updateProfile(String matricule, UpdateProfileRequest request);

    Adherent getProfile(String matricule);

    // 🔥 NOUVEAU (PRO - basé sur JWT)
    Adherent getProfileByEmail(String email);

    void updateProfileByEmail(String email, UpdateProfileRequest request);
}