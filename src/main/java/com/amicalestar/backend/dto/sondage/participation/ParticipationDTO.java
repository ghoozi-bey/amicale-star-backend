package com.amicalestar.backend.dto.sondage.participation;

import lombok.Data;

import java.util.List;

@Data
public class ParticipationDTO {
    private String nom;
    private String prenom;
    private String email;
    private List<ReponseDTO> reponses;
}
