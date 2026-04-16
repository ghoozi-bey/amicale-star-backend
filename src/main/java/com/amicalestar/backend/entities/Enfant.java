package com.amicalestar.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enfant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String dateNaissance;

    private String passportPath; // 📄 fichier enfant

    @ManyToOne
    @JoinColumn(name = "inscription_id")
    private Inscription inscription;
}