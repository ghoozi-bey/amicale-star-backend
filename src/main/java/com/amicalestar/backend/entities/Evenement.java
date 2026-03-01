package com.amicalestar.backend.entities;

import com.amicalestar.backend.enums.StatutEvenement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "evenements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private String description;

    private String lieu;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private Double prix;

    private Integer nbPlaces;

    @Enumerated(EnumType.STRING)
    private StatutEvenement statut;
}
