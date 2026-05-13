package com.amicalestar.backend.dto.election;

import lombok.Data;

@Data
public class ElectionStatsDTO {

    private Long candidatId;

    private String nom;

    private String prenom;

    private String departement;

    private long votes;
}
