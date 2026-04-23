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
    private Integer nbEnfantsMoins12;
    private Integer nbEnfantsMoins18;
    private Boolean estCouple;
    public InscriptionDTO(
            Long id,
            String statut,
            String modePaiement,
            String statutPaiement,
            Long eventId,
            String titre
    ) {
        this.id = id;
        this.statut = statut;
        this.modePaiement = modePaiement;
        this.statutPaiement = statutPaiement;
        this.id = eventId;
        this.titreEvenement = titre;
    }

}