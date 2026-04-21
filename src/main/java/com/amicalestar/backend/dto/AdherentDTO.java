package com.amicalestar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdherentDTO {

    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String cin;
    private String typeAdherent;
    private String departement;

    private String photoUrl; // ✅ juste URL, pas image
}