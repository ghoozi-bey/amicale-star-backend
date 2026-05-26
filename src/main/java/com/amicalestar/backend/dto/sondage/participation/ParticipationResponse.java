package com.amicalestar.backend.dto.sondage.participation;

import lombok.Data;

import java.util.List;

@Data
public class ParticipationResponse {

    private boolean hasParticipated;
    private List<QuestionAnswer> answers;

    // === Réponse enregistrée pour une question ===
    @Data
    public static class QuestionAnswer {

        private Long questionId;
        private List<Long> choixIds;
        private String texte;
    }
}