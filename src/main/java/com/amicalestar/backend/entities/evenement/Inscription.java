package com.amicalestar.backend.entities.evenement;

import com.amicalestar.backend.entities.Adherent;
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

    // Chargement lazy et exclusion du JSON
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adherent_id")
    @JsonIgnore
    private Adherent adherent;

    // Chargement lazy et exclusion du JSON
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    @JsonIgnore
    private Evenement evenement;

    // Stockage du passeport avec chargement lazy
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    @Column(name = "passport")
    private byte[] passport;

    // === Initialisation automatique avant insertion ===
    @PrePersist
    public void prePersist() {

        this.dateInscription = LocalDateTime.now();

        if (this.statut == null)
            this.statut = "EN_ATTENTE";
    }

    @OneToMany(mappedBy = "inscription", fetch = FetchType.LAZY)
    private List<Enfant> enfants;

    @OneToOne(mappedBy = "inscription", fetch = FetchType.LAZY)
    private Conjoint conjoint;

    private Integer nbEnfantsMoins12;
    private Integer nbEnfantsMoins18;
    private Boolean estCouple;

    private Double prixTotal;
    private Double remiseAppliquee;

    // Suppression automatique des paiements liés
    @OneToMany(mappedBy = "inscription", cascade = CascadeType.ALL)
    private List<Paiement> paiements;

    private Double resteAPayer;
}