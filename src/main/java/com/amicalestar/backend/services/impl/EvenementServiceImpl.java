package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.entities.evenement.Evenement;
import com.amicalestar.backend.enums.StatutEvenement;
import com.amicalestar.backend.repositories.evenement.EvenementRepository;
import com.amicalestar.backend.services.interfaces.EvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.io.IOException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvenementServiceImpl implements EvenementService {

    private final EvenementRepository evenementRepository;

    @Override
    public Evenement createEvenement(Evenement evenement) {

        // 🔥 AJOUT ICI (DEBUG)
        System.out.println("REM12 ACTIVE = " + evenement.getRemiseEnfant12Active());
        System.out.println("REM12 % = " + evenement.getRemiseEnfant12Pourcentage());

        evenement.setStatut(StatutEvenement.ACTIF);
        System.out.println("🔥 CREATE EVENEMENT EXECUTE 🔥");

        // =========================
        // 🔥 LOGIQUE isInternational (FIX FINAL)
        // =========================
        if (evenement.getTypeEvenement() != null &&
                evenement.getTypeEvenement().getId() != null) {

            Long typeId = evenement.getTypeEvenement().getId();

            // ✅ OMRA / HAJ → toujours true
            if (typeId == 1) {
                evenement.setIsInternational(true);
            }
            // ✅ CONVENTION → toujours false
            else if (typeId == 3) {
                evenement.setIsInternational(false);
            }
            // ✅ VOYAGE → valeur envoyée par le front
            else if (typeId == 2) {
                if (evenement.getIsInternational() == null) {
                    evenement.setIsInternational(false);
                }
            }
        }

        // =========================
        // 🔥 IMAGE PAR DÉFAUT
        // =========================
        if (evenement.getPhoto() == null || evenement.getPhoto().length == 0) {

            String type = "";

            if (evenement.getTypeEvenement() != null &&
                    evenement.getTypeEvenement().getNom() != null) {

                type = evenement.getTypeEvenement().getNom().toUpperCase();
            }

            try {
                InputStream is;

                if (type.contains("OMRA") || type.contains("HAJJ")) {
                    is = new ClassPathResource("static/default/HAJJetOMRA.jpg").getInputStream();
                }
                else if (type.contains("VOYAGE")) {
                    is = new ClassPathResource("static/default/voyage.jpg").getInputStream();
                }
                else {
                    is = new ClassPathResource("static/default/convention.png").getInputStream();
                }

                evenement.setPhoto(is.readAllBytes());
                evenement.setPhotoType("image/jpeg");

            } catch (IOException e) {
                throw new RuntimeException("Erreur image par défaut", e);
            }
        }

        return evenementRepository.save(evenement);
    }

    @Override
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    @Override
    public Evenement getEvenementById(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));
    }

    @Override
    public List<Evenement> getMesEvenements(String matricule) {
        return evenementRepository.findEventsWhereUserParticipates(matricule);
    }

    @Override
    public List<Evenement> getMesInscriptions(Long matricule) {
        return evenementRepository.findEvenementsByAdherentInscrit(matricule);
    }

    @Override
    public List<Evenement> getEvenementsCrees(String matricule) {
        return evenementRepository.findByAdherent_Matricule(matricule);
    }

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
    public Evenement updateEvenement(Long id, Evenement evenement) {

        Evenement existing = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));

        if (evenement.getPrix() != null) {
            existing.setPrix(evenement.getPrix());
        }

        if (evenement.getTitre() != null) {
            existing.setTitre(evenement.getTitre());
        }

        // 🔥 mise à jour image si fournie
        if (evenement.getPhoto() != null && evenement.getPhoto().length > 0) {
            existing.setPhoto(evenement.getPhoto());
        }

        if (evenement.getPhotoType() != null) {
            existing.setPhotoType(evenement.getPhotoType());
        }

        return evenementRepository.save(existing);
    }
    @Override
    public void deleteEvenement(Long id) {

        Evenement event = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));

        if (event.getInscriptions() != null && !event.getInscriptions().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer : il y a des inscriptions");
        }

        evenementRepository.delete(event);
    }
}