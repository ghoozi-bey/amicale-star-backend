package com.amicalestar.backend.entities;

import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "adherents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adherent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAdherent;

    private String nom;

    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    private String motdepasse;

    @Pattern(regexp = "\\d{8}")
    @Column(unique = true, length = 8)
    private String cin;

    @Pattern(regexp = "STAR\\d{6}")
    @Column(unique = true, nullable = false, length = 10)
    private String matricule;

    @Temporal(TemporalType.DATE)
    @Column(updatable = false)
    private Date dateinscription;

    @Enumerated(EnumType.STRING)
    private TypeAdherent typeAdherent;

    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    @Pattern(regexp = "\\d{8}")
    @Column(unique = true, length = 8)
    private String telephone;

    @Enumerated(EnumType.STRING)
    private Departement departement;

    private Boolean actif;

    @Column(length = 255)
    private String photoProfil;

    @PrePersist
    public void prePersist() {
        this.dateinscription = new Date();

        if (this.actif == null) {
            this.actif = true;
        }
    }

}