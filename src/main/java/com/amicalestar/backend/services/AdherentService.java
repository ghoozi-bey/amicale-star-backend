package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Adherent;

import java.util.List;

public interface AdherentService {

    Adherent createAdherent(Adherent adherent);

    List<Adherent> getAllAdherents();

    Adherent getAdherentById(String matricule);

    Adherent updateAdherent(String matricule, Adherent adherent);

    void deleteAdherent(String matricule);

}