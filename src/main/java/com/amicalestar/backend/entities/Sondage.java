package com.amicalestar.backend.entities;

import com.amicalestar.backend.enums.StatutSondage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Sondage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private LocalDateTime dateCreation;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    private StatutSondage statut;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Adherent createdBy;

    @OneToMany(mappedBy = "sondage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        this.statut = StatutSondage.BROUILLON;
    }

    @OneToMany(mappedBy = "sondage")
    private List<Participation> participations = new ArrayList<>();
}
