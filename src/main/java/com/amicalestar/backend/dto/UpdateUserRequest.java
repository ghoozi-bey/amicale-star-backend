package com.amicalestar.backend.dto;

import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.enums.Departement;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
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

    private Long typeEvenementId;

    // 🔥 VERSION CORRECTE
    private MultipartFile photoProfil;
}