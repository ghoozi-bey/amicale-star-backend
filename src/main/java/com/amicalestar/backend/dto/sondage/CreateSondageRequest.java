package com.amicalestar.backend.dto.sondage;

import com.amicalestar.backend.enums.TypeQuestion;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateSondageRequest {

    private String title;
    private String description;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    private List<QuestionRequest> questions;

    // === Informations d’une question du sondage ===
    @Data
    public static class QuestionRequest {

        private String text;

        @Enumerated(EnumType.STRING)
        private TypeQuestion type;

        private List<String> choix;
        private Boolean required;
    }
}