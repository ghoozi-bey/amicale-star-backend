package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.CreateSondageRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.Choix;
import com.amicalestar.backend.entities.Sondage;
import com.amicalestar.backend.enums.StatutSondage;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.SondageRepository;
import com.amicalestar.backend.services.SondageService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class SondageServiceImpl implements SondageService {

    private final SondageRepository sondageRepository;
    private final AdherentRepository adherentRepository;

    public SondageServiceImpl(SondageRepository sondageRepository,
                              AdherentRepository adherentRepository) {
        this.sondageRepository = sondageRepository;
        this.adherentRepository = adherentRepository;
    }

    @Override
    public Sondage createSondage(CreateSondageRequest request) {

        // 🔒 validation logique
        if (request.getDateDebut().isAfter(request.getDateFin())) {
            throw new RuntimeException("dateDebut doit être avant dateFin");
        }

        // 🔐 récupérer user depuis JWT
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Adherent user = adherentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 📦 création sondage
        Sondage sondage = new Sondage();
        sondage.setQuestion(request.getQuestion());
        sondage.setDateDebut(request.getDateDebut());
        sondage.setDateFin(request.getDateFin());
        sondage.setStatut(StatutSondage.BROUILLON);
        sondage.setCreatedBy(user);

        // 🔥 création des choix
        List<Choix> choixList = request.getOptions().stream()
                .map(label -> {
                    Choix choix = new Choix();
                    choix.setLabel(label);
                    choix.setSondage(sondage); // CRITICAL
                    return choix;
                })
                .toList();

        sondage.setChoix(choixList);

        return sondageRepository.save(sondage);
    }
}
