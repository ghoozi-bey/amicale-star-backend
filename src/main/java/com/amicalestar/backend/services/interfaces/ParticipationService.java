package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.sondage.participation.ParticipationRequest;
import com.amicalestar.backend.dto.sondage.participation.ParticipationResponse;

public interface ParticipationService {

    // === Enregistrement d’une participation au sondage ===
    void submitParticipation(ParticipationRequest request, String email);

    // === Récupération de la participation utilisateur ===
    ParticipationResponse getUserParticipation(Long sondageId, String email);
}