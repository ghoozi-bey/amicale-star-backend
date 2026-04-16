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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InscriptionServiceImpl implements InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final AdherentRepository adherentRepository;
    private final EvenementRepository evenementRepository;
    private final ConjointRepository conjointRepository;   // ✅ AJOUT
    private final EnfantRepository enfantRepository;       // ✅ AJOUT

    // ✅ ANCIEN (on garde)
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

    // ✅ NOUVELLE MÉTHODE (🔥 IMPORTANT)
    @Override
    public void createInscription(InscriptionRequest request,
                                  MultipartFile conjointFile,
                                  List<MultipartFile> enfantsFiles) {

        // 🔥 DEBUG
        System.out.println("Matricule reçu: " + request.getMatricule());
        System.out.println("Event ID reçu: " + request.getEvenementId());

        // 1. USER (CORRIGÉ ✅)
        Adherent adherent = adherentRepository.findById(request.getMatricule())
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé ❌"));

        // 2. EVENT (SAFE ✅)
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

        // 4. CONJOINT
        if (request.getConjoint() != null) {

            ConjointDTO dto = request.getConjoint();

            Conjoint conjoint = new Conjoint();
            conjoint.setNom(dto.getNom());
            conjoint.setPrenom(dto.getPrenom());
            conjoint.setCin(dto.getCin());
            conjoint.setTelephone(dto.getTelephone());
            conjoint.setInscription(inscription);

            conjointRepository.save(conjoint);
        }

        // 5. ENFANTS
        if (request.getEnfants() != null) {

            for (EnfantDTO dto : request.getEnfants()) {

                Enfant enfant = new Enfant();
                enfant.setNom(dto.getNom());
                enfant.setPrenom(dto.getPrenom());
                enfant.setInscription(inscription);

                enfantRepository.save(enfant);
            }
        }

        System.out.println("✅ INSCRIPTION CRÉÉE AVEC SUCCÈS");
    }

    // ✅ GET
    @Override
    public List<Inscription> getInscriptionsAdherent(String matricule) {
        return inscriptionRepository.findByAdherentMatricule(matricule);
    }

    // ✅ SAVE FILE
    private String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);

            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Erreur upload fichier");
        }
    }
}