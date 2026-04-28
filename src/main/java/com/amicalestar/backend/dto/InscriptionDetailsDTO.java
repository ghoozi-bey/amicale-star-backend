package com.amicalestar.backend.dto;

import lombok.*;

import java.util.List;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class InscriptionDetailsDTO {

        private Long id;
        private String statut;

        // Adherent
        private String nom;
        private String prenom;
        private String email;
        private String telephone;

        // Event
        private String titre;
        private Double prix;
        private Double prixTotal;
        private String modePaiement;
        private String statutPaiement;

        // Conjoint
        private String conjointNom;

        // Enfants
        private List<String> enfants;
        private Long evenementId;
        private List<PaiementDTO> paiements;
    }

