package com.amicalestar.backend.dto.evenement;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class PaiementDTO {

    private Long id;
    private Double montant;
    private String modePaiement;
    private String statut;
    private LocalDate datePaiement;
    private boolean hasJustificatif;

    // === Constructeur du DTO paiement ===
    public PaiementDTO(
            Long id,
            Double montant,
            String modePaiement,
            String statut,
            LocalDate datePaiement,
            boolean hasJustificatif
    ) {

        this.id = id;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.datePaiement = datePaiement;
        this.hasJustificatif = hasJustificatif;
    }
}