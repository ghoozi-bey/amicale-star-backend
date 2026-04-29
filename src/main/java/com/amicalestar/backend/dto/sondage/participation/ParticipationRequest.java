package com.amicalestar.backend.dto.sondage.participation;

import lombok.Data;
import java.util.List;

@Data
public class ParticipationRequest {

    private Long sondageId;
    private List<QuestionAnswer> answers;

    @Data
    public static class QuestionAnswer {
        private Long questionId;
        private List<Long> choixIds;
        private String texte;
    }
}