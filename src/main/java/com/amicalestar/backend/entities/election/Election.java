package com.amicalestar.backend.entities.election;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.StatutElection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 3000)
    private String description;

    private LocalDateTime dateCreation;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    private StatutElection statut;

    private Integer nombreCandidats;

    private Integer nombreGagnants;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Adherent createdBy;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidat> candidats = new ArrayList<>();

    @PrePersist
    public void prePersist() {

        this.dateCreation = LocalDateTime.now();

        if(this.statut == null) {
            this.statut = StatutElection.BROUILLON;
        }
    }
}