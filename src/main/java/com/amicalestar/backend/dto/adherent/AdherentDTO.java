package com.amicalestar.backend.dto.adherent;

import lombok.Data;

@Data
public class AdherentDTO {

    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String cin;
    private String typeAdherent;
    private String dateNaissance;
    private Long typeEvenementId;
    private String departement;
    private Boolean actif;

    private String photoUrl;
    private boolean hasPhoto;

    public AdherentDTO(
            String matricule,
            String nom,
            String prenom,
            String email,
            String telephone,
            String cin,
            String typeAdherent,
            String departement,
            String photoUrl,
            boolean hasPhoto
    ) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.cin = cin;
        this.typeAdherent = typeAdherent;
        this.departement = departement;
        this.photoUrl = photoUrl;
        this.hasPhoto = hasPhoto;
    }
}