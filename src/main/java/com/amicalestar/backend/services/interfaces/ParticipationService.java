package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.sondage.participation.ParticipationRequest;
import com.amicalestar.backend.dto.sondage.participation.ParticipationResponse;

public interface ParticipationService {
    void submitParticipation(ParticipationRequest request, String email);
    ParticipationResponse getUserParticipation(Long sondageId, String email);
}

