package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.entities.evenement.TypeEvenement;

import java.util.List;

public interface TypeEvenementService {

    // par nom
    TypeEvenement findByNom(String nom);

    // par id
    TypeEvenement findById(Long id);

    List<TypeEvenement> getAll();
}