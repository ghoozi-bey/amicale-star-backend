package com.amicalestar.backend.entities.election;

import com.amicalestar.backend.entities.Adherent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        // Empêche un adhérent de candidater plusieurs fois à la même élection
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"adherent_id", "election_id"}
                )
        }
)
public class Candidat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "adherent_id")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    private LocalDateTime dateCandidature;

    // === Initialisation automatique de la date de candidature ===
    @PrePersist
    public void prePersist() {

        this.dateCandidature = LocalDateTime.now();
    }
}