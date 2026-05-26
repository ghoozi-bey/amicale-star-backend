package com.amicalestar.backend.dto.evenement;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionDTO {

    // Informations de l’inscription
    private Long id;
    private String statut;

    // Informations de l’événement
    private Long evenementId;
    private String titreEvenement;

    // Informations de la famille
    private Integer nbEnfantsMoins12;
    private Integer nbEnfantsMoins18;
    private Boolean estCouple;

    // === Constructeur personnalisé ===
    public InscriptionDTO(
            Long id,
            String statut,
            Long evenementId,
            String titre
    ) {

        this.id = id;
        this.statut = statut;
        this.evenementId = evenementId;
        this.titreEvenement = titre;
    }
}