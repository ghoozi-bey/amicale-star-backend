package com.amicalestar.backend.entities.evenement;

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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "passport")
    private byte[] passport;

    @ManyToOne
    @JoinColumn(name = "inscription_id")
    private Inscription inscription;
}