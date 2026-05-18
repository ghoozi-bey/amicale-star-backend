package com.amicalestar.backend.dto.adherent;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Data
public class UpdateProfileRequest {

    private String nom;
    private String prenom;

    @Email(message = "Email invalide")
    private String email;

    @Pattern(regexp = "\\d{8}", message = "Le téléphone doit contenir exactement 8 chiffres")
    private String telephone;

    private String currentPassword;

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String newPassword;

    private MultipartFile photoProfil;

    private String removePhoto;
}