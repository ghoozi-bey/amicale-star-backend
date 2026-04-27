package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.ParticipationRequest;
import com.amicalestar.backend.entities.*;
import com.amicalestar.backend.enums.StatutSondage;
import com.amicalestar.backend.enums.TypeQuestion;
import com.amicalestar.backend.repositories.*;
import com.amicalestar.backend.services.ParticipationService;
import com.amicalestar.backend.services.SondageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Get sondage
        Sondage sondage = sondageRepository.findById(request.getSondageId())
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        // 3. Check status
        sondageService.updateStatut(sondage);
        if (sondage.getStatut() != StatutSondage.ACTIF) {
            throw new RuntimeException("Sondage not active");
        }

        // 4. Prevent duplicate participation
        if (participationRepository.existsByAdherentAndSondage(user, sondage)) {
            throw new RuntimeException("User already participated");
        }

        // 5. Create participation
        Participation participation = new Participation();
        participation.setAdherent(user);
        participation.setSondage(sondage);

        List<Reponse> reponses = new ArrayList<>();

        // 6. Process answers
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("No answers provided");
        }

        for (ParticipationRequest.QuestionAnswer qa : request.getAnswers()) {

            Question question = questionRepository.findById(qa.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            if (question.getRequired()) {

                boolean answered =
                        (qa.getChoixIds() != null && !qa.getChoixIds().isEmpty()) ||
                                (qa.getTexte() != null && !qa.getTexte().isBlank());

                if (!answered) {
                    throw new RuntimeException("Question '" + question.getText() + "' is required");
                }
            }

            // Validate belongs to sondage
            if (!question.getSondage().getId().equals(sondage.getId())) {
                throw new RuntimeException("Invalid question for this sondage");
            }

            switch (question.getType()) {

                case CHOIX_UNIQUE -> {

                    if (qa.getChoixIds() == null || qa.getChoixIds().size() != 1) {
                        throw new RuntimeException("Invalid single choice");
                    }

                    Choix choix = choixRepository.findById(qa.getChoixIds().get(0))
                            .orElseThrow(() -> new RuntimeException("Choix not found"));

                    if (!choix.getQuestion().getId().equals(question.getId())) {
                        throw new RuntimeException("Choix does not belong to question");
                    }

                    if (qa.getTexte() != null) {
                        throw new RuntimeException("Choices question cannot have text");
                    }

                    Reponse r = new Reponse();
                    r.setParticipation(participation);
                    r.setQuestion(question);
                    r.setChoix(choix);

                    reponses.add(r);
                }

                case CHOIX_MULTIPLE -> {

                    if (qa.getChoixIds() == null || qa.getChoixIds().isEmpty()) {
                        throw new RuntimeException("Choices required");
                    }

                    if (qa.getTexte() != null) {
                        throw new RuntimeException("Choices question cannot have text");
                    }

                    Set<Long> uniqueChoixIds = new HashSet<>(qa.getChoixIds());

                    for (Long choixId : uniqueChoixIds) {

                        Choix choix = choixRepository.findById(choixId)
                                .orElseThrow(() -> new RuntimeException("Choix not found"));

                        if (!choix.getQuestion().getId().equals(question.getId())) {
                            throw new RuntimeException("Invalid choix for question");
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
                        throw new RuntimeException("Text answer required");
                    }

                    if (qa.getChoixIds() != null && !qa.getChoixIds().isEmpty()) {
                        throw new RuntimeException("Text question cannot have choices");
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

}