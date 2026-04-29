package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.Election.CreateElectionRequest;
import com.amicalestar.backend.entities.Election;

import java.util.List;

public interface ElectionService {

    Election create(CreateElectionRequest request, String createdById);

    List<Election> getAll();

    Election getById(Long id);

    Election update(Long id, CreateElectionRequest request);

    void delete(Long id);

}
