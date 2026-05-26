package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.sondage.participation.ParticipationDTO;
import com.amicalestar.backend.dto.sondage.stats.SondageStatsDTO;

import java.util.List;

public interface SondageStatsService {

    // === Statistiques d’un sondage ===
    SondageStatsDTO getStats(Long sondageId);

    // === Liste des participations d’un sondage ===
    List<ParticipationDTO> getParticipations(Long sondageId);
}