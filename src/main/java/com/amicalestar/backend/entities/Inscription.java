package com.amicalestar.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "inscription", fetch = FetchType.LAZY)
    private List<Enfant> enfants;

    @OneToOne(mappedBy = "inscription", fetch = FetchType.LAZY)
    private Conjoint conjoint;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "justificatif_virement")
    private byte[] justificatifVirement;

    @Column(name = "justificatif_type")
    private String justificatifType;// ex: application/pdf

    private Integer nbEnfantsMoins12;
    private Integer nbEnfantsMoins18;
    private Boolean estCouple;

    private Double prixTotal;
    private Double remiseAppliquee;
}