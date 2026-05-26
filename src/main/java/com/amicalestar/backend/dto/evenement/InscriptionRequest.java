package com.amicalestar.backend.dto.evenement;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionRequest {

    // Informations de l’utilisateur
    private String matricule;
    private Long evenementId;

    // Informations du paiement principal
    private String modePaiement;

    // Informations du conjoint (optionnel)
    private ConjointDTO conjoint;

    // Liste des enfants (optionnel)
    private List<EnfantDTO> enfants;

    // Informations du paiement en avance
    private Double avance;
    private String modePaiementAvance;
    private Integer nombreMois;
    private String dateDebutPaiement;
    private String modePaiementEcheance;
}