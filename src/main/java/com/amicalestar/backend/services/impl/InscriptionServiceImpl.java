package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.InscriptionRequest;
import com.amicalestar.backend.dto.EnfantDTO;
import com.amicalestar.backend.dto.ConjointDTO;
import com.amicalestar.backend.entities.*;
import com.amicalestar.backend.repositories.*;
import com.amicalestar.backend.services.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.amicalestar.backend.dto.InscriptionDTO;

@Service
@RequiredArgsConstructor
public class InscriptionServiceImpl implements InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final AdherentRepository adherentRepository;
    private final EvenementRepository evenementRepository;
    private final ConjointRepository conjointRepository;
    private final EnfantRepository enfantRepository;

    @Override
    public Inscription inscrire(String matricule, Long eventId) {

        Adherent adherent = adherentRepository.findById(matricule)
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé"));

        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));

        Inscription inscription = Inscription.builder()
                .adherent(adherent)
                .evenement(evenement)
                .build();

        return inscriptionRepository.save(inscription);
    }

    @Override
    public void createInscription(InscriptionRequest request,
                                  MultipartFile adherentFile,
                                  MultipartFile conjointFile,
                                  List<MultipartFile> enfantsFiles) {

        // =========================
        // 1. ADHERENT
        // =========================
        Adherent adherent = adherentRepository.findById(request.getMatricule())
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé"));

        // =========================
        // 2. EVENEMENT
        // =========================
        Evenement evenement = evenementRepository.findById(request.getEvenementId())
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));

        // =========================
        // 🔥 FIX IMPORTANT : déterminer type
        // =========================
        boolean isVoyage = evenement.getTypeEvenement() != null &&
                evenement.getTypeEvenement().getNom().equalsIgnoreCase("VOYAGE");

        boolean isExterne = Boolean.TRUE.equals(evenement.getIsInternational());

        // =========================
        // 🔥 VALIDATION AVANT SAVE
        // =========================
        if (isVoyage && isExterne) {

            if (adherentFile == null || adherentFile.isEmpty()) {
                throw new RuntimeException("Passeport obligatoire pour voyage externe ❌");
            }

            if (request.getConjoint() != null &&
                    (conjointFile == null || conjointFile.isEmpty())) {
                throw new RuntimeException("Passeport conjoint obligatoire ❌");
            }

            if (request.getEnfants() != null && !request.getEnfants().isEmpty()) {
                if (enfantsFiles == null || enfantsFiles.isEmpty()) {
                    throw new RuntimeException("Passeports enfants obligatoires ❌");
                }

                for (MultipartFile file : enfantsFiles) {
                    if (file == null || file.isEmpty()) {
                        throw new RuntimeException("Passeport enfant obligatoire ❌");
                    }
                }
            }
        }

        // =========================
        // 3. CALCUL NOMBRE PERSONNES
        // =========================
        int nombrePersonnes = 1;

        if (request.getConjoint() != null) nombrePersonnes++;
        if (request.getEnfants() != null) nombrePersonnes += request.getEnfants().size();

        // =========================
        // 4. PLACES
        // =========================
        if (evenement.getNbPlaces() < nombrePersonnes) {
            throw new RuntimeException("Pas assez de places disponibles ❌");
        }

        evenement.setNbPlaces(evenement.getNbPlaces() - nombrePersonnes);
        evenementRepository.save(evenement);

        // =========================
        // 5. INSCRIPTION
        // =========================
        Inscription inscription = Inscription.builder()
                .adherent(adherent)
                .evenement(evenement)
                .modePaiement(request.getModePaiement())
                .statut("EN_ATTENTE")
                .statutPaiement("NON_PAYE")
                .build();

        if (adherentFile != null && !adherentFile.isEmpty()) {
            try {
                inscription.setPassport(adherentFile.getBytes());
            } catch (Exception e) {
                throw new RuntimeException("Erreur lecture fichier adherent", e);
            }
        }

        inscriptionRepository.save(inscription);

        // =========================
        // 6. CONJOINT
        // =========================
        if (request.getConjoint() != null) {

            ConjointDTO dto = request.getConjoint();

            Conjoint conjoint = new Conjoint();
            conjoint.setNom(dto.getNom());
            conjoint.setPrenom(dto.getPrenom());
            conjoint.setCin(dto.getCin());
            conjoint.setTelephone(dto.getTelephone());
            conjoint.setDateNaissance(
                    dto.getDateNaissance() != null ? dto.getDateNaissance().toString() : null
            );
            conjoint.setInscription(inscription);

            if (conjointFile != null && !conjointFile.isEmpty()) {
                try {
                    conjoint.setPassport(conjointFile.getBytes());
                } catch (Exception e) {
                    throw new RuntimeException("Erreur fichier conjoint", e);
                }
            }

            conjointRepository.save(conjoint);
        }

        // =========================
        // 7. ENFANTS
        // =========================
        if (request.getEnfants() != null && !request.getEnfants().isEmpty()) {

            for (int i = 0; i < request.getEnfants().size(); i++) {

                EnfantDTO dto = request.getEnfants().get(i);

                Enfant enfant = new Enfant();
                enfant.setNom(dto.getNom());
                enfant.setPrenom(dto.getPrenom());
                enfant.setDateNaissance(dto.getDateNaissance());
                enfant.setInscription(inscription);

                if (enfantsFiles != null && i < enfantsFiles.size()) {
                    MultipartFile file = enfantsFiles.get(i);

                    if (file != null && !file.isEmpty()) {
                        try {
                            enfant.setPassport(file.getBytes());
                        } catch (Exception e) {
                            throw new RuntimeException("Erreur fichier enfant", e);
                        }
                    }
                }

                enfantRepository.save(enfant);
            }
        }
    }

    @Override
    @Transactional
    public List<InscriptionDTO> getInscriptionsAdherent(String matricule) {
        return inscriptionRepository.findDTOByMatricule(matricule);
    }
}