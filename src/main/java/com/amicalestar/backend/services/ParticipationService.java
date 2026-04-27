package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.ParticipationRequest;
import com.amicalestar.backend.entities.Sondage;

public interface ParticipationService {
    void submitParticipation(ParticipationRequest request, String email);
}

