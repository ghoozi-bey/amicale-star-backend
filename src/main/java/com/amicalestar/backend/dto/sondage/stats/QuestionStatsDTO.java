package com.amicalestar.backend.dto.sondage.stats;

import lombok.Data;

import java.util.List;

@Data
public class QuestionStatsDTO {
    private Long questionId;
    private String questionText;
    private List<ChoixStatsDTO> choix;
}
