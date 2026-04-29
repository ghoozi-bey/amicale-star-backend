package com.amicalestar.backend.dto.adherent;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateProfileRequest {

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    private String currentPassword;
    private String newPassword;

    // important: même nom que backend (cohérence)
    private MultipartFile photoProfil;

    private String removePhoto;
}