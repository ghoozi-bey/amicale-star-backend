package com.amicalestar.backend.entities.evenement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;

    private String modePaiement;

    private LocalDate datePaiement;

    private String statut;

    // Chargement lazy et exclusion du JSON
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscription_id")
    @JsonIgnore
    private Inscription inscription;

    // Stockage du justificatif avec chargement lazy
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private byte[] justificatifVirement;

    private Boolean justificatifValide = false;

}