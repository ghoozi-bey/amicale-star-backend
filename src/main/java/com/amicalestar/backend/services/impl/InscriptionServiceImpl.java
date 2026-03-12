package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.entities.*;
import com.amicalestar.backend.repositories.*;
import com.amicalestar.backend.services.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InscriptionServiceImpl implements InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final AdherentRepository adherentRepository;
    private final EvenementRepository evenementRepository;

    @Override
    public Inscription inscrire(String matricule, Long eventId) {

        Adherent adherent = adherentRepository.findById(matricule)
                .orElseThrow();

        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow();

        Inscription inscription = Inscription.builder()
                .adherent(adherent)
                .evenement(evenement)
                .build();

        return inscriptionRepository.save(inscription);
    }

    @Override
    public List<Inscription> getInscriptionsAdherent(String matricule) {

        return inscriptionRepository.findByAdherentMatricule(matricule);

    }
}