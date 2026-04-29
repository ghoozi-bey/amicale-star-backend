package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.election.CreateElectionRequest;
import com.amicalestar.backend.entities.election.Election;

import java.util.List;

public interface ElectionService {

    Election create(CreateElectionRequest request, String createdById);

    List<Election> getAll();

    Election getById(Long id);

    Election update(Long id, CreateElectionRequest request);

    void delete(Long id);

}
