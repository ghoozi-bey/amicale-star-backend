package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.Participation.ParticipationDTO;
import com.amicalestar.backend.dto.stats.SondageStatsDTO;

import java.util.List;

public interface SondageStatsService {

    SondageStatsDTO getStats(Long sondageId);

    List<ParticipationDTO> getParticipations(Long sondageId);
}
