package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Adherent;

import java.util.List;

public interface AdherentService {

    Adherent createAdherent(Adherent adherent);

    List<Adherent> getAllAdherents();

    Adherent getAdherentById(Long id);

    Adherent updateAdherent(Long id, Adherent adherent);

    void deleteAdherent(Long id);

}