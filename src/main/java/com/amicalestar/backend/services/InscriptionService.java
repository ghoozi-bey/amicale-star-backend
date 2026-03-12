package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Inscription;

import java.util.List;

public interface InscriptionService {

    Inscription inscrire(String matricule, Long eventId);

    List<Inscription> getInscriptionsAdherent(String matricule);
}