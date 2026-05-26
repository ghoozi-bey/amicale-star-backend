package com.amicalestar.backend.dto.evenement;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnfantFullDTO {

    private String nom;
    private String prenom;

    private String dateNaissance;

    private String passeport;
}