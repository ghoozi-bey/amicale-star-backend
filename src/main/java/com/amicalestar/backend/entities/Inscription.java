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

    @ManyToOne
    @JoinColumn(name = "adherent_id")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @PrePersist
    public void prePersist() {
        this.dateInscription = LocalDateTime.now();
        this.statut = "EN_ATTENTE";
    }
}
