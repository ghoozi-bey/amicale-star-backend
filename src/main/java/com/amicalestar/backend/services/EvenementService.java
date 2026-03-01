package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.Evenement;

import java.util.List;

public interface EvenementService {

    Evenement createEvenement(Evenement evenement);

    List<Evenement> getAllEvenements();

    Evenement archiverEvenement(Long id);
}