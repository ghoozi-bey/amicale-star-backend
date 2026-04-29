package com.amicalestar.backend.dto.evenement;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionFullDTO {

    private Long id;

    // 🔹 ADHERENT
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String cin;

    // 🔹 EVENEMENT
    private String titre;
    private Double prix;
    private String typeEvenement;

    // 🔹 STATUT
    private String statut;

    // 🔹 FAMILLE (DTO FULL)
    private ConjointFullDTO conjoint;
    private List<EnfantFullDTO> enfants;

    // 🔹 PASSEPORT ADHERENT
    private String passeport;

    // 🔹 PRIX TOTAL
    private Double prixTotal;
    private List<PaiementDTO> paiements;
}