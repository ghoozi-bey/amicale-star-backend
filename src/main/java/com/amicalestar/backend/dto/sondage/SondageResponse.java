package com.amicalestar.backend.dto.sondage;

import com.amicalestar.backend.enums.StatutSondage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SondageResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private StatutSondage statut;
    private String createdBy;
    private List<QuestionResponse> questions;
}