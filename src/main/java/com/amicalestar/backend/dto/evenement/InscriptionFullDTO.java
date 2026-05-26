package com.amicalestar.backend.dto.evenement;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionFullDTO {

    // Informations de l’inscription
    private Long id;

    // Informations de l’adhérent
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String cin;

    // Informations de l’événement
    private String titre;
    private Double prix;
    private String typeEvenement;

    // Statut de l’inscription
    private String statut;

    // Informations de la famille
    private ConjointFullDTO conjoint;
    private List<EnfantFullDTO> enfants;

    // Passeport de l’adhérent
    private String passeport;

    // Informations du paiement total
    private Double prixTotal;
    private List<PaiementDTO> paiements;
}