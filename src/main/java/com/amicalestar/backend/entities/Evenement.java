package com.amicalestar.backend.entities;

import com.amicalestar.backend.enums.StatutEvenement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "evenements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private String lieu;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Double prix;
    private Integer nbPlaces;

    // 🔥 REMPLACEMENT (String -> BLOB)
    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_type")
    private String photoType;

    @Enumerated(EnumType.STRING)
    private StatutEvenement statut;

    @ManyToOne
    @JoinColumn(name = "type_evenement_id")
    private TypeEvenement typeEvenement;

    @ManyToOne
    @JoinColumn(name = "adherent_id", referencedColumnName = "matricule")
    private Adherent adherent;

    private String societe;
    private String agence;
    private String destination;

    @JsonIgnore
    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL)
    private List<Inscription> inscriptions;
}