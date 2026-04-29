package com.amicalestar.backend.dto.sondage.stats;

import lombok.Data;

import java.util.List;

@Data
public class SondageStatsDTO {
    private int totalParticipants;
    private List<QuestionStatsDTO> questions;
}
