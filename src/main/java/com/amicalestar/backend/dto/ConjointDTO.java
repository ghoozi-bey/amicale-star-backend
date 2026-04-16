package com.amicalestar.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConjointDTO {

    private String nom;
    private String prenom;
    private String dateNaissance;
    private String cin;
    private String telephone;
}