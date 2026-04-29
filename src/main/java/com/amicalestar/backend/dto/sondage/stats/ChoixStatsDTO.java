package com.amicalestar.backend.dto.sondage.stats;

import lombok.Data;

@Data
public class ChoixStatsDTO {
    private Long choixId;
    private String label;
    private int count;
    private double percentage;
}
