package com.amicalestar.backend.entities.sondage;

import com.amicalestar.backend.entities.Adherent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"adherent_id", "sondage_id"})
)
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Participant
    @ManyToOne
    @JoinColumn(name = "adherent_id", nullable = false)
    private Adherent adherent;

    // Sondage
    @ManyToOne
    @JoinColumn(name = "sondage_id", nullable = false)
    private Sondage sondage;

    // Reponses
    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reponse> reponses = new ArrayList<>();
}