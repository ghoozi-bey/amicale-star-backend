package com.amicalestar.backend.dto.evenement;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionRequest {

    // 👤 utilisateur
    private String matricule;
    private Long evenementId;

    // 💳 paiement principal
    private String modePaiement;

    // 👩 conjoint (optionnel)
    private ConjointDTO conjoint;

    // 👶 enfants (optionnel)
    private List<EnfantDTO> enfants;

    // =========================
    // 🔥 NOUVEAU (AVANCE)
    // =========================
    private Double avance;                 // montant payé au début
    private String modePaiementAvance;// cash / virement / carte
    private Integer nombreMois;
    private String dateDebutPaiement;
    private String modePaiementEcheance;
}