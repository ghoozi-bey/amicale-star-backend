package com.amicalestar.backend.dto.Participation;

import lombok.Data;

import java.util.List;

@Data
public class ParticipationDTO {
    private String nom;
    private String email;
    private List<ReponseDTO> reponses;
}
