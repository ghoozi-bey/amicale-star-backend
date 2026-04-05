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

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutSondage statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Adherent createdBy;

    @OneToMany(mappedBy = "sondage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choix> choix = new ArrayList<>();

    @OneToMany(mappedBy = "sondage")
    private List<Vote> votes;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }
}
