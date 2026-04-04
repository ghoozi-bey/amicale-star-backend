package com.amicalestar.backend.dto;

import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Email(message = "Email invalide")
    private String email;

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @Pattern(regexp = "\\d{8}", message = "Le CIN doit contenir exactement 8 chiffres")
    private String cin;

    private TypeAdherent typeAdherent;
    private Date dateNaissance;

    @Pattern(regexp = "\\d{8}", message = "Le téléphone doit contenir exactement 8 chiffres")
    private String telephone;
    private Departement departement;
    private Boolean actif;
    private String photoProfil;
    private Long typeEvenementId;

}