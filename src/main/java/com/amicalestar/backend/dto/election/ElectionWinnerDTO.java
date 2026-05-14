package com.amicalestar.backend.dto.election;

import lombok.Data;

@Data
public class ElectionWinnerDTO {

    private String matricule;

    private String nom;

    private String prenom;

    private String departement;

    private Long votes;
}