package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.Participation.ParticipationRequest;
import com.amicalestar.backend.dto.Participation.ParticipationResponse;

public interface ParticipationService {
    void submitParticipation(ParticipationRequest request, String email);
    ParticipationResponse getUserParticipation(Long sondageId, String email);
}

