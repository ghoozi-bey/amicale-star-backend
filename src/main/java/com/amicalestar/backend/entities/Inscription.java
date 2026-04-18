package com.amicalestar.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateInscription;

    private String statut;

    private String modePaiement;
    private String statutPaiement;

    // ✅ LAZY + IGNORE (IMPORTANT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adherent_id")
    @JsonIgnore
    private Adherent adherent;

    // ✅ LAZY + IGNORE (TRÈS IMPORTANT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    @JsonIgnore
    private Evenement evenement;

    // ✅ PASSEPORT (PARFAIT)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    @Column(name = "passport")
    private byte[] passport;

    @PrePersist
    public void prePersist() {
        this.dateInscription = LocalDateTime.now();

        if (this.statut == null)
            this.statut = "EN_ATTENTE";

        if (this.statutPaiement == null)
            this.statutPaiement = "NON_PAYE";
    }
}