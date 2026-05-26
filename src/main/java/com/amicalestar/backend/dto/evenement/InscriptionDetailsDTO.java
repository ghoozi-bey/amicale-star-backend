package com.amicalestar.backend.dto.evenement;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InscriptionDetailsDTO {

    // Informations de l’inscription
    private Long id;
    private String statut;

    // Informations de l’adhérent
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // Informations de l’événement
    private String titre;
    private Double prix;
    private Double prixTotal;
    private String modePaiement;
    private String statutPaiement;

    // Informations du conjoint
    private String conjointNom;

    // Informations des enfants
    private List<String> enfants;

    // Identifiant de l’événement
    private Long evenementId;

    // Liste des paiements
    private List<PaiementDTO> paiements;
}