package com.amicalestar.backend.dto;

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

    // 💳 paiement
    private String modePaiement;

    // 👩 conjoint (optionnel)
    private ConjointDTO conjoint;

    // 👶 enfants (optionnel)
    private List<EnfantDTO> enfants;
}