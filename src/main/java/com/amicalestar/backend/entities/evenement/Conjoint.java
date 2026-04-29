package com.amicalestar.backend.entities.evenement;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "conjoint")
public class Conjoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "date_naissance")
    private String dateNaissance;

    @Column(name = "cin")
    private String cin;

    @Column(name = "telephone")
    private String telephone;

    @Lob
    @Column(name = "passport")
    private byte[] passport;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscription_id", unique = true)
    private Inscription inscription;
}