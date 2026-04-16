package com.amicalestar.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conjoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String dateNaissance;
    private String cin;
    private String telephone;

    private String passportPath; // 📄 fichier

    @OneToOne
    @JoinColumn(name = "inscription_id")
    private Inscription inscription;
}