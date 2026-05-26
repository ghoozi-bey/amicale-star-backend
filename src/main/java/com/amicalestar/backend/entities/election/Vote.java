package com.amicalestar.backend.entities.election;

import com.amicalestar.backend.entities.Adherent;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        // Empêche un vote dupliqué pour le même candidat
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "election_id",
                                "voter_id",
                                "candidat_id"
                        }
                )
        }
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    @ManyToOne
    @JoinColumn(name = "voter_id")
    private Adherent voter;

    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    private LocalDateTime votedAt;
}