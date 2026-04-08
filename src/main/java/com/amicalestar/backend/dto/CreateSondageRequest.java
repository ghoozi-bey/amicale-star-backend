package com.amicalestar.backend.dto;

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

    @Data
    public static class QuestionRequest {
        private String text;
        private List<String> choix;
    }
}