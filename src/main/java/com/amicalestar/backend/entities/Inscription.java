package com.amicalestar.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Column;
import lombok.*;
import jakarta.persistence.*;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.Evenement;

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

    private String modePaiement;      // ✅ AJOUT
    private String statutPaiement;    // ✅ AJOUT

    @ManyToOne
    @JoinColumn(name = "adherent_id")
    @JsonIgnore
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @PrePersist
    public void prePersist() {
        this.dateInscription = LocalDateTime.now();

        if (this.statut == null)
            this.statut = "EN_ATTENTE";

        if (this.statutPaiement == null)
            this.statutPaiement = "NON_PAYE";
    }
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    @Column(name = "passport")
    private byte[] passport;

}