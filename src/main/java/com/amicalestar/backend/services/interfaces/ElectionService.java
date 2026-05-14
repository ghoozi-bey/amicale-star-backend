package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.election.*;
import com.amicalestar.backend.entities.election.Election;

import java.util.List;

public interface ElectionService {

    ElectionResponseDTO create(CreateElectionRequest request, String email);

    List<ElectionResponseDTO> getAll();

    ElectionResponseDTO getById(Long id);

    ElectionResponseDTO update(Long id, CreateElectionRequest request);

    void delete(Long id);

    void updateStatut(Election e);

    void publish(Long id);

    void unpublish(Long id);

    void reject(Long id);

    List<AdherentLiteDTO> getEligibleAdherents(Long electionId);

    List<ElectionResponseDTO> getActiveElections();

    ElectionResponseDTO getActiveElectionById(Long id);

    List<ElectionStatsDTO> getStats(Long electionId);

    void attribuerRoles(Long electionId, List<AttribuerRoleDTO> request);

    List<ElectionWinnerDTO> getElectionWinners(Long electionId);

}
