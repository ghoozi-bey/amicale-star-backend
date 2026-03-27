package com.amicalestar.backend.entities;

import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "adherents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adherent {

    @Id
    @Pattern(regexp = "STAR\\d{6}")
    @Column(length = 10)
    private String matricule;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Pattern(regexp = "\\d{8}")
    @Column(unique = true, length = 8)
    private String cin;

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

    // 🔥 AJOUT IMPORTANT (relation avec type evenement)
    @ManyToOne
    @JoinColumn(name = "type_evenement_id")
    private TypeEvenement typeEvenement;

    @JsonIgnore
    @OneToMany(mappedBy = "adherent")
    private List<Inscription> inscriptions;

    @PrePersist
    public void prePersist() {
        this.dateinscription = new Date();
        if (this.actif == null) {
            this.actif = true;
        }
    }

    public String getRoleName() {
        return "ROLE_" + this.typeAdherent.name();
    }
}