package com.amicalestar.backend.dto.evenement;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConjointFullDTO {

    private String nom;
    private String prenom;
    private String dateNaissance;
    private String cin;
    private String telephone;
    private String passeport;
}