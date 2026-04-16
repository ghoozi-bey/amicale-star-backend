package com.amicalestar.backend.dto;

import com.amicalestar.backend.entities.Evenement;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionDTO {

    private String statut;
    private String modePaiement;      // ✅ AJOUT
    private String statutPaiement;    // ✅ AJOUT
    private Evenement evenement;

}