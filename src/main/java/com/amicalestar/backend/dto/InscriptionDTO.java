package com.amicalestar.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionDTO {

    private Long id;

    private String statut;


    private Long evenementId;
    private String titreEvenement;
    private Integer nbEnfantsMoins12;
    private Integer nbEnfantsMoins18;
    private Boolean estCouple;

    // 🔥 CONSTRUCTEUR CORRIGÉ
    public InscriptionDTO(
            Long id,
            String statut,
            Long evenementId,
            String titre
    ) {
        this.id = id;
        this.statut = statut;
        this.evenementId = evenementId; // ✅ FIX
        this.titreEvenement = titre;
    }
}