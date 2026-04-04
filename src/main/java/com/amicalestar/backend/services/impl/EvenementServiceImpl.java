package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.entities.Evenement;
import com.amicalestar.backend.enums.StatutEvenement;
import com.amicalestar.backend.repositories.EvenementRepository;
import com.amicalestar.backend.services.EvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvenementServiceImpl implements EvenementService {

    private final EvenementRepository evenementRepository;

    @Override
    public Evenement createEvenement(Evenement evenement) {
        evenement.setStatut(StatutEvenement.BROUILLON);
        return evenementRepository.save(evenement);
    }

    @Override
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    // 🔵 PARTICIPATION UNIQUEMENT
    @Override
    public List<Evenement> getMesEvenements(String matricule) {
        return evenementRepository.findEventsWhereUserParticipates(matricule);
    }

    // 🟡 EVENEMENTS CREES UNIQUEMENT
    @Override
    public List<Evenement> getEvenementsCrees(String matricule) {
        return evenementRepository.findByAdherent_Matricule(matricule);
    }

    // 🟢 DASHBOARD
    @Override
    public List<Evenement> getEvenementsActifs() {
        return evenementRepository.findByStatutNot(StatutEvenement.ARCHIVE);
    }

    @Override
    public Evenement archiverEvenement(Long id) {
        Evenement event = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));

        event.setStatut(StatutEvenement.ARCHIVE);
        return evenementRepository.save(event);
    }

    @Override
    public void deleteEvenement(Long id) {
        evenementRepository.deleteById(id);
    }

    @Override
    public Evenement updateEvenement(Long id, Evenement evenement) {

        Evenement existing = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));

        if (evenement.getPrix() != null) {
            existing.setPrix(evenement.getPrix());
        }

        if (evenement.getTitre() != null) {
            existing.setTitre(evenement.getTitre());
        }

        return evenementRepository.save(existing);
    }
}