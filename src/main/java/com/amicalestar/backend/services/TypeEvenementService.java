package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.TypeEvenement;

import java.util.List;

public interface TypeEvenementService {

    // par nom
    TypeEvenement findByNom(String nom);

    // par id
    TypeEvenement findById(Long id);

    List<TypeEvenement> getAll();
}