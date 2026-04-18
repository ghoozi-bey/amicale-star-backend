package com.amicalestar.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionDTO {

    private Long id; // ✅ IMPORTANT

    private String statut;
    private String modePaiement;
    private String statutPaiement;

    private Long evenementId;
    private String titreEvenement;
}