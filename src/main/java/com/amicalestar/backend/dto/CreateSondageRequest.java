package com.amicalestar.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateSondageRequest {

    @NotBlank
    private String question;

    @NotEmpty
    private List<String> options;

    @NotNull
    private LocalDateTime dateDebut;

    @NotNull
    private LocalDateTime dateFin;

}
