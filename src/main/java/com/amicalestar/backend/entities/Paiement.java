package com.amicalestar.backend.entities;

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

    // 🔥 CORRECTION ICI
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscription_id")
    @JsonIgnore
    private Inscription inscription;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private byte[] justificatifVirement; // base64 ou path
    private Boolean justificatifValide = false;

}