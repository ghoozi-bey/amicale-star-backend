package com.amicalestar.backend.dto.election;

import com.amicalestar.backend.enums.StatutElection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ElectionResponseDTO {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime dateCreation;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private StatutElection statut;

    private Integer nombreCandidats;

    private Integer nombreGagnants;

    private String createdByNom;

    private String createdByPrenom;

    private List<String> candidats;
}
