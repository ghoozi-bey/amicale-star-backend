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
    private Object departement;
    private Object typeAdherent;
}