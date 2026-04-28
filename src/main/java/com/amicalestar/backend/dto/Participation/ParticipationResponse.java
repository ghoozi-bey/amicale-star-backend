package com.amicalestar.backend.dto.Participation;

import lombok.Data;

import java.util.List;

@Data
public class ParticipationResponse {

    private boolean hasParticipated;
    private List<QuestionAnswer> answers;

    @Data
    public static class QuestionAnswer {
        private Long questionId;
        private List<Long> choixIds;
        private String texte;
    }
}