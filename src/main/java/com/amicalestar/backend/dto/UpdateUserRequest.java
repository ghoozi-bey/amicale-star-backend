package com.amicalestar.backend.dto;

import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String cin;
    private TypeAdherent typeAdherent;
    private Date dateNaissance;
    private String telephone;
    private Departement departement;
    private Boolean actif;
    private String photoProfil;
    private Long typeEvenementId;

}