package com.amicalestar.backend.dto.Election;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateElectionRequest {

    private String title;

    private String description;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

}
