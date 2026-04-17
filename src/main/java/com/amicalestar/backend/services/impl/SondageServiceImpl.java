package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.ChoixResponse;
import com.amicalestar.backend.dto.CreateSondageRequest;
import com.amicalestar.backend.dto.QuestionResponse;
import com.amicalestar.backend.dto.SondageResponse;
import com.amicalestar.backend.entities.*;
import com.amicalestar.backend.enums.StatutSondage;
import com.amicalestar.backend.enums.TypeQuestion;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.SondageRepository;
import com.amicalestar.backend.services.SondageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SondageServiceImpl implements SondageService {

    private final SondageRepository sondageRepository;
    private final AdherentRepository adherentRepository;

    @Override
    public Sondage createSondage(CreateSondageRequest request, String matricule) {

        Adherent creator = adherentRepository.findByEmail(matricule)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔒 validate dates
        if (request.getDateDebut().isAfter(request.getDateFin())) {
            throw new RuntimeException("dateDebut must be before dateFin");
        }

        Sondage sondage = new Sondage();
        sondage.setTitle(request.getTitle());
        sondage.setDescription(request.getDescription());
        sondage.setDateDebut(request.getDateDebut());
        sondage.setDateFin(request.getDateFin());
        sondage.setCreatedBy(creator);

        List<Question> questions = new ArrayList<>();

        for (CreateSondageRequest.QuestionRequest qdto : request.getQuestions()) {

            Question question = new Question();
            question.setText(qdto.getText());
            question.setType(qdto.getType()); // ✅ NEW
            question.setSondage(sondage);

            List<Choix> choixList = new ArrayList<>();

            // 🔥 Logic based on type
            if (qdto.getType() == TypeQuestion.TEXTE) {

                // TEXT question should NOT have choices
                if (qdto.getChoix() != null && !qdto.getChoix().isEmpty()) {
                    throw new RuntimeException("Text question cannot have choices");
                }

            } else {

                // CHOIX_UNIQUE or CHOIX_MULTIPLE must have choices
                if (qdto.getChoix() == null || qdto.getChoix().isEmpty()) {
                    throw new RuntimeException("Choices required for this question type");
                }

                for (String label : qdto.getChoix()) {
                    Choix choix = new Choix();
                    choix.setLabel(label);
                    choix.setQuestion(question);
                    choixList.add(choix);
                }
            }

            question.setChoixList(choixList);
            questions.add(question);
        }

        sondage.setQuestions(questions);

        return sondageRepository.save(sondage);
    }

    @Override
    public List<SondageResponse> getAllSondages() {

        List<Sondage> sondages = sondageRepository.findAll();

        sondages.forEach(this::updateStatut);

        sondageRepository.saveAll(sondages); // persist updates

        return sondages.stream().map(this::toResponse).toList();
    }

    @Override
    public List<SondageResponse> getSondagesByCreatorEmail(String email) {

        List<Sondage> sondages = sondageRepository.findByCreatedBy_Email(email);

        sondages.forEach(this::updateStatut);
        sondageRepository.saveAll(sondages);

        return sondages.stream().map(this::toResponse).toList();
    }

    @Override
    public SondageResponse getSondageById(Long id) {

        Sondage sondage = sondageRepository.findDetailedById(id)
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        updateStatut(sondage);
        sondageRepository.save(sondage);

        return toResponse(sondage);
    }

    private SondageResponse toResponse(Sondage s) {
        return new SondageResponse(
                s.getId(),
                s.getTitle(),
                s.getDescription(),
                s.getDateCreation(),
                s.getDateDebut(),
                s.getDateFin(),
                s.getStatut(),
                s.getCreatedBy() != null ? s.getCreatedBy().getEmail() : null,
                s.getQuestions() == null ? List.of() : s.getQuestions().stream().map(this::toQuestionResponse).toList()
        );
    }

    private QuestionResponse toQuestionResponse(Question q) {
        return new QuestionResponse(
                q.getId(),
                q.getText(),
                q.getType(),
                q.getChoixList() == null ? List.of() : q.getChoixList().stream().map(this::toChoixResponse).toList()
        );
    }

    private ChoixResponse toChoixResponse(Choix c) {
        return new ChoixResponse(c.getId(), c.getLabel());
    }

    public Sondage publierSondage(Long id) {
        Sondage sondage = sondageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        if (sondage.getStatut() != StatutSondage.BROUILLON) {
            throw new RuntimeException("Sondage already published");
        }

        sondage.setStatut(StatutSondage.PUBLISHED);

        return sondageRepository.save(sondage);
    }

    public Sondage annulerPublication(Long id) {

        Sondage sondage = sondageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        if (sondage.getStatut() != StatutSondage.PUBLISHED) {
            throw new RuntimeException("Only published sondage can be reverted");
        }

        sondage.setStatut(StatutSondage.BROUILLON);

        return sondageRepository.save(sondage);
    }

    public Sondage rejeterSondage(Long id) {

        Sondage sondage = sondageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sondage introuvable"));

        // Business rule: only BROUILLON can be rejected
        if (sondage.getStatut() != StatutSondage.BROUILLON) {
            throw new RuntimeException("Seuls les sondages en brouillon peuvent être rejetés");
        }

        sondage.setStatut(StatutSondage.REJECTED);

        return sondageRepository.save(sondage);
    }

    public void updateStatut(Sondage s) {

        // 🔒 Never touch final states
        if (s.getStatut() == StatutSondage.TERMINE ||
                s.getStatut() == StatutSondage.REJECTED) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // ❌ BROUILLON expired → REJECTED
        if (s.getStatut() == StatutSondage.BROUILLON &&
                (now.isEqual(s.getDateDebut()) || now.isAfter(s.getDateDebut()))) {

            s.setStatut(StatutSondage.REJECTED);
            return;
        }

        // 🟢 PUBLISHED → ACTIF
        if (s.getStatut() == StatutSondage.PUBLISHED &&
                (now.isEqual(s.getDateDebut()) || now.isAfter(s.getDateDebut())) &&
                now.isBefore(s.getDateFin())) {

            s.setStatut(StatutSondage.ACTIF);
            return;
        }

        // 🔴 ACTIF → TERMINE
        if (s.getStatut() == StatutSondage.ACTIF &&
                now.isAfter(s.getDateFin())) {

            s.setStatut(StatutSondage.TERMINE);
        }
    }

    @Override
    public Sondage updateSondage(Long id, CreateSondageRequest request) {

        Sondage sondage = sondageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        // update fields
        sondage.setTitle(request.getTitle());
        sondage.setDescription(request.getDescription());
        sondage.setDateDebut(request.getDateDebut());
        sondage.setDateFin(request.getDateFin());

        // ⚠️ handle questions (basic version)
        sondage.getQuestions().clear();

        request.getQuestions().forEach(q -> {
            Question question = new Question();
            question.setText(q.getText());
            question.setType(q.getType());
            question.setSondage(sondage);

            if (!q.getType().equals("TEXTE")) {
                List<Choix> choixList = q.getChoix().stream().map(label -> {
                    Choix c = new Choix();
                    c.setLabel(label);
                    c.setQuestion(question);
                    return c;
                }).toList();

                question.setChoixList(choixList);
            }

            sondage.getQuestions().add(question);
        });

        return sondageRepository.save(sondage);
    }
}