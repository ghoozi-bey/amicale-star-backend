package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.entities.evenement.TypeEvenement;

import java.util.List;

public interface TypeEvenementService {

    // === Recherche d’un type d’événement par nom ===
    TypeEvenement findByNom(String nom);

    // === Recherche d’un type d’événement par id ===
    TypeEvenement findById(Long id);

    // === Liste des types d’événements ===
    List<TypeEvenement> getAll();
}