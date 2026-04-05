package com.amicalestar.backend.entities;

import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    @Column(length = 10)
    private String matricule;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 8)
    private String cin;

    @Temporal(TemporalType.DATE)
    @Column(updatable = false)
    private Date dateinscription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAdherent typeAdherent;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dateNaissance;

    @Column(nullable = false, unique = true, length = 8)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Departement departement;

    private Boolean actif;

    @Column(length = 255)
    private String photoProfil;

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

    @JsonIgnore
    @OneToMany(mappedBy = "adherent")
    private List<Evenement> evenements;
}