package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.election.CreateElectionRequest;
import com.amicalestar.backend.dto.election.ElectionResponseDTO;
import com.amicalestar.backend.entities.election.Election;

import java.util.List;

public interface ElectionService {

    ElectionResponseDTO create(CreateElectionRequest request, String email);

    List<ElectionResponseDTO> getAll();

    ElectionResponseDTO getById(Long id);

    ElectionResponseDTO update(Long id, CreateElectionRequest request);

    void delete(Long id);

}
