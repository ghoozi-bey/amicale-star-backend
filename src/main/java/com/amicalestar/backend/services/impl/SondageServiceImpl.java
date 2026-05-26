package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.sondage.ChoixResponse;
import com.amicalestar.backend.dto.sondage.CreateSondageRequest;
import com.amicalestar.backend.dto.sondage.QuestionResponse;
import com.amicalestar.backend.dto.sondage.SondageResponse;
import com.amicalestar.backend.entities.*;
import com.amicalestar.backend.entities.sondage.Choix;
import com.amicalestar.backend.entities.sondage.Question;
import com.amicalestar.backend.entities.sondage.Sondage;
import com.amicalestar.backend.enums.StatutSondage;
import com.amicalestar.backend.enums.TypeQuestion;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.sondage.SondageRepository;
import com.amicalestar.backend.services.interfaces.SondageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class SondageServiceImpl implements SondageService {

    private final SondageRepository sondageRepository;
    private final AdherentRepository adherentRepository;

    // === Création sondage ===
    @Override
    public Sondage createSondage(CreateSondageRequest request, String matricule) {

        Adherent creator = adherentRepository.findByEmail(matricule)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validation dates
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
            question.setType(qdto.getType());
            question.setSondage(sondage);

            if (qdto.getRequired() == null) {
                question.setRequired(true);
            } else {
                question.setRequired(qdto.getRequired());
            }

            List<Choix> choixList = new ArrayList<>();

            // Gestion selon type question
            if (qdto.getType() == TypeQuestion.TEXTE) {

                // Une question texte ne doit pas contenir de choix
                if (qdto.getChoix() != null && !qdto.getChoix().isEmpty()) {
                    throw new RuntimeException("Text question cannot have choices");
                }

            } else {

                // Les questions à choix doivent contenir des choix
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

    // === Liste de tous les sondages ===
    @Override
    public List<SondageResponse> getAllSondages() {

        List<Sondage> sondages = sondageRepository.findAll();

        sondages.forEach(this::updateStatut);

        sondageRepository.saveAll(sondages);

        return sondages.stream()
                .map(this::toResponse)
                .toList();
    }

    // === Liste des sondages actifs ===
    @Override
    public List<SondageResponse> getActiveSondages() {

        List<Sondage> sondages = sondageRepository.findAll();

        // Mise à jour statuts
        sondages.forEach(this::updateStatut);

        sondageRepository.saveAll(sondages);

        // Filtrage actifs
        return sondages.stream()
                .filter(s -> s.getStatut() == StatutSondage.ACTIF)
                .map(this::toResponse)
                .toList();
    }

    // === Liste sondages créateur ===
    @Override
    public List<SondageResponse> getSondagesByCreatorEmail(String email) {

        List<Sondage> sondages =
                sondageRepository.findByCreatedBy_Email(email);

        sondages.forEach(this::updateStatut);

        sondageRepository.saveAll(sondages);

        return sondages.stream()
                .map(this::toResponse)
                .toList();
    }

    // === Détails sondage ===
    @Override
    public SondageResponse getSondageById(Long id) {

        Sondage sondage = sondageRepository.findDetailedById(id)
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        updateStatut(sondage);

        sondageRepository.save(sondage);

        return toResponse(sondage);
    }

    // === Détails sondage actif ===
    @Override
    public SondageResponse getActiveSondageById(Long id) {

        Sondage sondage = sondageRepository.findDetailedById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Sondage not found"
                ));

        updateStatut(sondage);

        // Vérification actif
        if (sondage.getStatut() != StatutSondage.ACTIF) {

            sondageRepository.save(sondage);

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Sondage is not active"
            );
        }

        return toResponse(sondage);
    }

    // === Conversion Sondage -> DTO ===
    private SondageResponse toResponse(Sondage s) {

        return new SondageResponse(
                s.getId(),
                s.getTitle(),
                s.getDescription(),
                s.getDateCreation(),
                s.getDateDebut(),
                s.getDateFin(),
                s.getStatut(),
                s.getCreatedBy() != null
                        ? s.getCreatedBy().getEmail()
                        : null,
                s.getQuestions() == null
                        ? List.of()
                        : s.getQuestions()
                        .stream()
                        .map(this::toQuestionResponse)
                        .toList()
        );
    }

    // === Conversion Question -> DTO ===
    private QuestionResponse toQuestionResponse(Question q) {

        return new QuestionResponse(
                q.getId(),
                q.getText(),
                q.getType(),
                q.getChoixList() == null
                        ? List.of()
                        : q.getChoixList()
                        .stream()
                        .map(this::toChoixResponse)
                        .toList(),
                q.getRequired()
        );
    }

    // === Conversion Choix -> DTO ===
    private ChoixResponse toChoixResponse(Choix c) {

        return new ChoixResponse(
                c.getId(),
                c.getLabel()
        );
    }

    // === Publication sondage ===
    public Sondage publierSondage(Long id) {

        Sondage sondage = sondageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        if (sondage.getStatut() != StatutSondage.BROUILLON) {
            throw new RuntimeException("Sondage already published");
        }

        sondage.setStatut(StatutSondage.PUBLISHED);

        return sondageRepository.save(sondage);
    }

    // === Annulation publication ===
    public Sondage annulerPublication(Long id) {

        Sondage sondage = sondageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        if (sondage.getStatut() != StatutSondage.PUBLISHED) {
            throw new RuntimeException("Only published sondage can be reverted");
        }

        sondage.setStatut(StatutSondage.BROUILLON);

        return sondageRepository.save(sondage);
    }

    // === Rejet sondage ===
    public void rejeterSondage(Long id) {

        Sondage sondage = sondageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sondage introuvable"));

        if (sondage.getStatut() != StatutSondage.BROUILLON) {
            throw new RuntimeException("Seuls les sondages en brouillon peuvent être rejetés");
        }

        sondage.setStatut(StatutSondage.REJECTED);

        sondageRepository.save(sondage);
    }

    // === Mise à jour statut sondage ===
    public void updateStatut(Sondage s) {

        // États finaux
        if (s.getStatut() == StatutSondage.TERMINE ||
                s.getStatut() == StatutSondage.REJECTED) {

            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // Brouillon expiré -> rejeté
        if (s.getStatut() == StatutSondage.BROUILLON &&
                (now.isEqual(s.getDateDebut()) || now.isAfter(s.getDateDebut()))) {

            s.setStatut(StatutSondage.REJECTED);

            return;
        }

        // Published -> actif
        if (s.getStatut() == StatutSondage.PUBLISHED &&
                (now.isEqual(s.getDateDebut()) || now.isAfter(s.getDateDebut())) &&
                now.isBefore(s.getDateFin())) {

            s.setStatut(StatutSondage.ACTIF);

            return;
        }

        // Actif -> terminé
        if (s.getStatut() == StatutSondage.ACTIF &&
                now.isAfter(s.getDateFin())) {

            s.setStatut(StatutSondage.TERMINE);
        }
    }

    // === Mise à jour sondage ===
    @Override
    public Sondage updateSondage(Long id, CreateSondageRequest request) {

        Sondage sondage = sondageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        sondage.setTitle(request.getTitle());
        sondage.setDescription(request.getDescription());
        sondage.setDateDebut(request.getDateDebut());
        sondage.setDateFin(request.getDateFin());

        sondage.getQuestions().clear();

        request.getQuestions().forEach(q -> {

            Question question = new Question();

            question.setText(q.getText());
            question.setType(q.getType());
            question.setSondage(sondage);

            if (q.getRequired() == null) {
                question.setRequired(true);
            } else {
                question.setRequired(q.getRequired());
            }

            if (q.getType() != TypeQuestion.TEXTE) {

                if (q.getChoix() == null || q.getChoix().isEmpty()) {
                    throw new RuntimeException("Choices required");
                }

                List<Choix> choixList = q.getChoix()
                        .stream()
                        .map(label -> {

                            Choix c = new Choix();

                            c.setLabel(label);
                            c.setQuestion(question);

                            return c;

                        }).collect(Collectors.toList());

                question.setChoixList(choixList);

            } else {

                question.setChoixList(new ArrayList<>());
            }

            sondage.getQuestions().add(question);
        });

        return sondageRepository.save(sondage);
    }

    // === Suppression sondage ===
    public void supprimerSondage(Long id) {

        Sondage sondage = sondageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sondage introuvable"));

        // Seuls les sondages rejetés peuvent être supprimés
        if (sondage.getStatut() != StatutSondage.REJECTED) {
            throw new RuntimeException("Seuls les sondages rejetés peuvent être supprimés");
        }

        sondageRepository.delete(sondage);
    }

}