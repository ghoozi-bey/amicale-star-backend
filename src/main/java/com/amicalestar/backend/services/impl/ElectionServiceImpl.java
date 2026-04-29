package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.Election.CreateElectionRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.Election;
import com.amicalestar.backend.enums.StatutElection;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.Election.ElectionRepository;
import com.amicalestar.backend.services.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final AdherentRepository adherentRepository;

    @Override
    public Election create(CreateElectionRequest request, String createdById) {

        if(request.getDateFin().isBefore(request.getDateDebut())) {
            throw new RuntimeException(
                    "La date de fin doit être après la date de début"
            );
        }

        Adherent adherent = adherentRepository.findById(createdById)
                .orElseThrow(() ->
                        new RuntimeException("Adhérent introuvable")
                );

        Election election = new Election();

        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setDateDebut(request.getDateDebut());
        election.setDateFin(request.getDateFin());

        election.setCreatedBy(adherent);

        election.setDateCreation(LocalDateTime.now());

        election.setStatut(StatutElection.BROUILLON);

        return electionRepository.save(election);
    }

    @Override
    public List<Election> getAll() {
        return electionRepository.findAll();
    }

    @Override
    public Election getById(Long id) {

        return electionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );
    }

    @Override
    public Election update(Long id, CreateElectionRequest request) {

        Election election = getById(id);

        if(request.getDateFin().isBefore(request.getDateDebut())) {
            throw new RuntimeException(
                    "La date de fin doit être après la date de début"
            );
        }

        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setDateDebut(request.getDateDebut());
        election.setDateFin(request.getDateFin());

        return electionRepository.save(election);
    }

    @Override
    public void delete(Long id) {

        Election election = getById(id);

        electionRepository.delete(election);
    }
}
