package com.amicalestar.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnfantFullDTO {

    private String nom;
    private String prenom;

    private String dateNaissance; // ✅ IMPORTANT

    private String passeport;
}