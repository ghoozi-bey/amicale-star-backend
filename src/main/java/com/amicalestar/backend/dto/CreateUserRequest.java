package com.amicalestar.backend.dto;

import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import lombok.Data;

import java.util.Date;

@Data
public class CreateUserRequest {

    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String password;

    private String cin;
    private String telephone;

    private Date dateNaissance;

    private Departement departement;

    private TypeAdherent typeAdherent;
}