package com.amicalestar.backend.dto.election;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateElectionRequest {

    private String title;

    private String description;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private Integer nombreCandidats;

    private Integer nombreGagnants;

    private List<String> candidats;

}
