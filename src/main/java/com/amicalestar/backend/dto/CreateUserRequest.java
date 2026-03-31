package com.amicalestar.backend.dto;

import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank(message = "Le matricule est obligatoire")
    @Pattern(regexp = "STAR\\d{6}", message = "Format invalide (ex: STAR123456)")
    private String matricule;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotBlank(message = "Le CIN est obligatoire")
    @Pattern(regexp = "\\d{8}", message = "Le CIN doit contenir exactement 8 chiffres")
    private String cin;

    @NotNull(message = "Le type adhérent est obligatoire")
    private TypeAdherent typeAdherent;

    @NotNull(message = "La date de naissance est obligatoire")
    private Date dateNaissance;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "\\d{8}", message = "Le téléphone doit contenir exactement 8 chiffres")
    private String telephone;

    @NotNull(message = "Le département est obligatoire")
    private Departement departement;

    private Boolean actif;

    private String photoProfil;

    private Long typeEvenementId;
}