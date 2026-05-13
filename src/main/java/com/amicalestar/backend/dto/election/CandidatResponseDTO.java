package com.amicalestar.backend.dto.election;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidatResponseDTO {

    private Long id;

    private Long electionId;

    private String nom;

    private String prenom;

    private String matricule;

    private String Departement;
}