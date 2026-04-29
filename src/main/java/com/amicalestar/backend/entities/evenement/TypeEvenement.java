package com.amicalestar.backend.entities.evenement;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "types_evenement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeEvenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
}