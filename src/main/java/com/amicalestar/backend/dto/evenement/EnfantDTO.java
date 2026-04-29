package com.amicalestar.backend.dto.evenement;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnfantDTO {

    private String nom;
    private String prenom;
    private String dateNaissance;
}