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

import java.util.List;

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
                .orElseThrow();

        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow();

        Inscription inscription = Inscription.builder()
                .adherent(adherent)
                .evenement(evenement)
                .build();

        return inscriptionRepository.save(inscription);
    }

    // 🔥 VERSION FINALE AVEC BYTE[]
    @Override
    public void createInscription(InscriptionRequest request,
                                  MultipartFile conjointFile,
                                  List<MultipartFile> enfantsFiles) {

        System.out.println("Matricule reçu: " + request.getMatricule());
        System.out.println("Event ID reçu: " + request.getEvenementId());

        // 1. USER
        Adherent adherent = adherentRepository.findById(request.getMatricule())
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé ❌"));

        // 2. EVENT
        Evenement evenement = evenementRepository.findById(request.getEvenementId())
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé ❌"));

        // 3. INSCRIPTION
        Inscription inscription = Inscription.builder()
                .adherent(adherent)
                .evenement(evenement)
                .modePaiement(request.getModePaiement())
                .statut("EN_ATTENTE")
                .statutPaiement("NON_PAYE")
                .build();

        inscriptionRepository.save(inscription);

        // =========================
        // ✅ CONJOINT
        // =========================
        if (request.getConjoint() != null) {

            ConjointDTO dto = request.getConjoint();

            Conjoint conjoint = new Conjoint();
            conjoint.setNom(dto.getNom());
            conjoint.setPrenom(dto.getPrenom());
            conjoint.setCin(dto.getCin());
            conjoint.setTelephone(dto.getTelephone());
            conjoint.setDateNaissance(
                    dto.getDateNaissance() != null
                            ? dto.getDateNaissance().toString()
                            : null
            ); // ✅ FIX
            conjoint.setInscription(inscription);

            // ✅ PASSEPORT BYTE[]
            if (conjointFile != null && !conjointFile.isEmpty()) {
                try {
                    conjoint.setPassport(conjointFile.getBytes());
                } catch (Exception e) {
                    throw new RuntimeException("Erreur lecture fichier conjoint");
                }
            }
            System.out.println("DTO CONJOINT: " + dto);
            System.out.println("FILE NULL ? " + (conjointFile == null));
            conjointRepository.save(conjoint);
        }

        // =========================
        // ✅ ENFANTS
        // =========================
        if (request.getEnfants() != null && !request.getEnfants().isEmpty()) {

            for (int i = 0; i < request.getEnfants().size(); i++) {

                EnfantDTO dto = request.getEnfants().get(i);

                Enfant enfant = new Enfant();
                enfant.setNom(dto.getNom());
                enfant.setPrenom(dto.getPrenom());
                enfant.setDateNaissance(dto.getDateNaissance());
                enfant.setInscription(inscription);

                // ✅ PASSEPORT BYTE[]
                if (enfantsFiles != null && i < enfantsFiles.size()) {
                    MultipartFile file = enfantsFiles.get(i);

                    if (file != null && !file.isEmpty()) {
                        try {
                            enfant.setPassport(file.getBytes());
                        } catch (Exception e) {
                            throw new RuntimeException("Erreur lecture fichier enfant");
                        }
                    }
                }

                enfantRepository.save(enfant);
            }
        }

        System.out.println("✅ INSCRIPTION CRÉÉE AVEC SUCCÈS");
    }

    @Override
    public List<Inscription> getInscriptionsAdherent(String matricule) {
        return inscriptionRepository.findByAdherentMatricule(matricule);
    }
}