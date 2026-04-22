package com.amicalestar.backend.dto;

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

    // 🔹 PAIEMENT
    private String modePaiement;
    private String statutPaiement;

    // 🔹 STATUT
    private String statut;

    // 🔹 FAMILLE (DTO FULL)
    private ConjointFullDTO conjoint;
    private List<EnfantFullDTO> enfants;

    // 🔹 PASSEPORT ADHERENT
    private String passeport;

    // 🔹 PRIX TOTAL
    private Double prixTotal;
}