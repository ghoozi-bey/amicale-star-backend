package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.entities.TypeEvenement;
import com.amicalestar.backend.repositories.TypeEvenementRepository;
import com.amicalestar.backend.services.TypeEvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TypeEvenementServiceImpl implements TypeEvenementService {

    private final TypeEvenementRepository typeEvenementRepository;

    // 🔥 recherche par nom
    @Override
    public TypeEvenement findByNom(String nom) {
        return typeEvenementRepository.findByNom(nom)
                .orElseThrow(() -> new RuntimeException("TypeEvenement introuvable: " + nom));
    }

    // 🔥 recherche par ID (FIX PRINCIPAL)
    @Override
    public TypeEvenement findById(Long id) {
        return typeEvenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TypeEvenement introuvable avec id: " + id));
    }
}