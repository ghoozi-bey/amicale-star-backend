package com.amicalestar.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaiementDTO {

    private Long id;
    private Double montant;
    private String modePaiement;
    private String statut;
    private String datePaiement;
    private boolean hasJustificatif;
}