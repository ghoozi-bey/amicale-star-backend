package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.sondage.participation.ParticipationRequest;
import com.amicalestar.backend.dto.sondage.participation.ParticipationResponse;
import com.amicalestar.backend.entities.*;
import com.amicalestar.backend.entities.sondage.*;
import com.amicalestar.backend.enums.StatutSondage;
import com.amicalestar.backend.repositories.*;
import com.amicalestar.backend.repositories.sondage.ChoixRepository;
import com.amicalestar.backend.repositories.sondage.ParticipationRepository;
import com.amicalestar.backend.repositories.sondage.QuestionRepository;
import com.amicalestar.backend.repositories.sondage.SondageRepository;
import com.amicalestar.backend.services.interfaces.ParticipationService;
import com.amicalestar.backend.services.interfaces.SondageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final AdherentRepository adherentRepository;
    private final SondageRepository sondageRepository;
    private final QuestionRepository questionRepository;
    private final ChoixRepository choixRepository;
    private final ParticipationRepository participationRepository;
    private final SondageService sondageService;

    @Override
    public void submitParticipation(ParticipationRequest request, String email) {

        // 1. Get user
        Adherent user = adherentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 2. Get sondage
        Sondage sondage = sondageRepository.findById(request.getSondageId())
                .orElseThrow(() -> new RuntimeException("Sondage introuvable"));

        // 3. Check status
        sondageService.updateStatut(sondage);
        if (sondage.getStatut() != StatutSondage.ACTIF) {
            throw new RuntimeException("Ce sondage n'est pas actif");
        }

        // 4. Prevent duplicate participation
        Participation existing = participationRepository
                .findByAdherentAndSondage(user, sondage)
                .orElse(null);

        if (existing != null) {
            participationRepository.delete(existing);
        }

        // 5. Create participation
        Participation participation = new Participation();
        participation.setAdherent(user);
        participation.setSondage(sondage);

        List<Reponse> reponses = new ArrayList<>();

        // 6. Process answers
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("Aucune réponse fournie");
        }

        for (ParticipationRequest.QuestionAnswer qa : request.getAnswers()) {

            Question question = questionRepository.findById(qa.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question introuvable"));

            if (question.getRequired()) {

                boolean answered =
                        (qa.getChoixIds() != null && !qa.getChoixIds().isEmpty()) ||
                                (qa.getTexte() != null && !qa.getTexte().isBlank());

                if (!answered) {
                    throw new RuntimeException("La question \"" + question.getText() + "\" est obligatoire");
                }
            }

            // Validate belongs to sondage
            if (!question.getSondage().getId().equals(sondage.getId())) {
                throw new RuntimeException("Question invalide pour ce sondage");
            }

            switch (question.getType()) {

                case CHOIX_UNIQUE -> {

                    if (qa.getChoixIds() == null || qa.getChoixIds().size() != 1) {
                        throw new RuntimeException("Choix unique invalide");
                    }

                    Choix choix = choixRepository.findById(qa.getChoixIds().get(0))
                            .orElseThrow(() -> new RuntimeException("Choix introuvable"));

                    if (!choix.getQuestion().getId().equals(question.getId())) {
                        throw new RuntimeException("Ce choix n'appartient pas à la question");
                    }

                    if (qa.getTexte() != null) {
                        throw new RuntimeException("Une question à choix unique ne peut pas contenir de texte");
                    }

                    Reponse r = new Reponse();
                    r.setParticipation(participation);
                    r.setQuestion(question);
                    r.setChoix(choix);

                    reponses.add(r);
                }

                case CHOIX_MULTIPLE -> {

                    if (qa.getChoixIds() == null || qa.getChoixIds().isEmpty()) {
                        throw new RuntimeException("Veuillez sélectionner au moins un choix");
                    }

                    if (qa.getTexte() != null) {
                        throw new RuntimeException("Une question à choix multiple ne peut pas contenir de texte");
                    }

                    Set<Long> uniqueChoixIds = new HashSet<>(qa.getChoixIds());

                    for (Long choixId : uniqueChoixIds) {

                        Choix choix = choixRepository.findById(choixId)
                                .orElseThrow(() -> new RuntimeException("Choix introuvable"));

                        if (!choix.getQuestion().getId().equals(question.getId())) {
                            throw new RuntimeException("Choix invalide pour cette question");
                        }

                        Reponse r = new Reponse();
                        r.setParticipation(participation);
                        r.setQuestion(question);
                        r.setChoix(choix);

                        reponses.add(r);
                    }
                }

                case TEXTE -> {

                    if (qa.getTexte() == null || qa.getTexte().isBlank()) {
                        throw new RuntimeException("Une réponse textuelle est requise");
                    }

                    if (qa.getChoixIds() != null && !qa.getChoixIds().isEmpty()) {
                        throw new RuntimeException("Une question textuelle ne peut pas contenir de choix");
                    }

                    Reponse r = new Reponse();
                    r.setParticipation(participation);
                    r.setQuestion(question);
                    r.setTexte(qa.getTexte());

                    reponses.add(r);
                }
            }
        }

        // 7. Save everything
        participation.setReponses(reponses);
        participationRepository.save(participation);
    }

    @Override
    public ParticipationResponse getUserParticipation(Long sondageId, String email) {

        Adherent user = adherentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Sondage sondage = sondageRepository.findById(sondageId)
                .orElseThrow(() -> new RuntimeException("Sondage introuvable"));

        ParticipationResponse response = new ParticipationResponse();

        Participation participation = participationRepository
                .findByAdherentAndSondage(user, sondage)
                .orElse(null);

        if (participation == null) {
            response.setHasParticipated(false);
            return response;
        }

        response.setHasParticipated(true);

        List<ParticipationResponse.QuestionAnswer> answers = new ArrayList<>();

        Map<Long, ParticipationResponse.QuestionAnswer> map = new HashMap<>();

        for (Reponse r : participation.getReponses()) {

            Long qId = r.getQuestion().getId();

            ParticipationResponse.QuestionAnswer qa = map.get(qId);

            if (qa == null) {
                qa = new ParticipationResponse.QuestionAnswer();
                qa.setQuestionId(qId);
                qa.setChoixIds(new ArrayList<>());
                map.put(qId, qa);
            }

            // TEXT
            if (r.getTexte() != null) {
                qa.setTexte(r.getTexte());
            }

            // CHOIX
            if (r.getChoix() != null) {
                qa.getChoixIds().add(r.getChoix().getId());
            }
        }

        response.setAnswers(new ArrayList<>(map.values()));

        return response;
    }
}