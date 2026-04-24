package com.amicalestar.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InscriptionListDTO {

    private Long id;
    private String nom;
    private String email;

    private String modePaiement;   // depuis paiement
    private String statutPaiement; // depuis paiement

    private String statut;         // inscription
}