package com.amicalestar.backend.entities;

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
    private String description;
    private String lieu;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Double prix;
    private Integer nbPlaces;

    @JsonIgnore
    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;

    @Column(name = "photo_type")
    private String photoType;

    @Enumerated(EnumType.STRING)
    private StatutEvenement statut;

    // 🔥 IMPORTANT : garder JsonIgnore mais exposer ID
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_evenement_id")
    private TypeEvenement typeEvenement;

    // ✅ EXPOSER TYPE ID (TRÈS IMPORTANT)
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

    @JsonIgnore
    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inscription> inscriptions;

    @Column(name = "is_international")
    private Boolean isInternational;

    // ✅ FORCE JSON OUTPUT (IMPORTANT)
    @JsonProperty("isInternational")
    public Boolean getIsInternationalSafe() {
        return isInternational != null ? isInternational : false;
    }
}