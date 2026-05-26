package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.entities.evenement.TypeEvenement;
import com.amicalestar.backend.repositories.evenement.TypeEvenementRepository;
import com.amicalestar.backend.services.interfaces.TypeEvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeEvenementServiceImpl implements TypeEvenementService {

    private final TypeEvenementRepository typeEvenementRepository;

    // === Recherche type événement par nom ===
    @Override
    public TypeEvenement findByNom(String nom) {

        return typeEvenementRepository.findByNom(nom)
                .orElseThrow(() ->
                        new RuntimeException(
                                "TypeEvenement introuvable: " + nom
                        )
                );
    }

    // === Recherche type événement par ID ===
    @Override
    public TypeEvenement findById(Long id) {

        return typeEvenementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "TypeEvenement introuvable avec id: " + id
                        )
                );
    }

    // === Liste des types événements ===
    @Override
    public List<TypeEvenement> getAll() {

        return typeEvenementRepository.findAll();
    }
}