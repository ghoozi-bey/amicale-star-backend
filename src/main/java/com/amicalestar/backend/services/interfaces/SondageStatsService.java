package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.sondage.participation.ParticipationDTO;
import com.amicalestar.backend.dto.sondage.stats.SondageStatsDTO;

import java.util.List;

public interface SondageStatsService {

    SondageStatsDTO getStats(Long sondageId);

    List<ParticipationDTO> getParticipations(Long sondageId);
}
