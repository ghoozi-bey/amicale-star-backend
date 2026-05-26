package com.amicalestar.backend.entities.evenement;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.StatutEvenement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Column(columnDefinition = "TEXT")
    private String description;

    private String lieu;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Double prix;
    private Integer nbPlaces;

    // Stockage de l’image en base de données
    @JsonIgnore
    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;

    @Column(name = "photo_type")
    private String photoType;

    @Enumerated(EnumType.STRING)
    private StatutEvenement statut;

    // Relation ignorée dans le JSON pour éviter la boucle infinie
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_evenement_id")
    private TypeEvenement typeEvenement;

    // === Exposition de l’identifiant du type d’événement ===
    @JsonProperty("typeEvenementId")
    public Long getTypeEvenementId() {

        return typeEvenement != null ? typeEvenement.getId() : null;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adherent_id", referencedColumnName = "matricule")
    private Adherent adherent;

    private String societe;
    private String agence;
    private String destination;

    // Chargement lazy des inscriptions liées à l’événement
    @JsonIgnore
    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inscription> inscriptions;

    @Column(name = "is_international")
    private Boolean isInternational;

    // === Retour sécurisé du statut international ===
    @JsonProperty("isInternational")
    public Boolean getIsInternationalSafe() {

        return isInternational != null ? isInternational : false;
    }

    // Remise enfant moins de 12 ans
    private Boolean remiseEnfant12Active;
    private Double remiseEnfant12Pourcentage;

    // Remise enfant moins de 18 ans
    private Boolean remiseEnfant18Active;
    private Double remiseEnfant18Pourcentage;

    // Remise couple
    private Boolean remiseCoupleActive;
    private Double remiseCouplePourcentage;
}