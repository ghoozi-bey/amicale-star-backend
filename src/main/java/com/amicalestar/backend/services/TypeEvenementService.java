package com.amicalestar.backend.services;

import com.amicalestar.backend.entities.TypeEvenement;

public interface TypeEvenementService {

    // 🔥 par nom (ancien)
    TypeEvenement findByNom(String nom);

    // 🔥 par id (nouveau)
    TypeEvenement findById(Long id);
}