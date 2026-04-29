package com.amicalestar.backend.entities.sondage;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Reponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // participation
    @ManyToOne
    @JoinColumn(name = "participation_id", nullable = false)
    private Participation participation;

    // question
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // question à choix unique ou multiple
    @ManyToOne
    @JoinColumn(name = "choix_id")
    private Choix choix;

    // question texte
    @Column(columnDefinition = "TEXT")
    private String texte;
}