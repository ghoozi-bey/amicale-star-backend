package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.election.CreateElectionRequest;
import com.amicalestar.backend.dto.election.ElectionResponseDTO;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.election.Election;
import com.amicalestar.backend.enums.StatutElection;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.Election.ElectionRepository;
import com.amicalestar.backend.services.interfaces.ElectionService;
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
    public ElectionResponseDTO create(CreateElectionRequest request, String email) {

        Adherent creator = adherentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Election election = new Election();

        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setDateDebut(request.getDateDebut());
        election.setDateFin(request.getDateFin());

        election.setCreatedBy(creator);

        Election saved = electionRepository.save(election);

        ElectionResponseDTO dto = new ElectionResponseDTO();

        dto.setId(saved.getId());
        dto.setTitle(saved.getTitle());
        dto.setDescription(saved.getDescription());
        dto.setDateCreation(saved.getDateCreation());
        dto.setDateDebut(saved.getDateDebut());
        dto.setDateFin(saved.getDateFin());
        dto.setStatut(saved.getStatut());

        dto.setCreatedByNom(saved.getCreatedBy().getNom());
        dto.setCreatedByPrenom(saved.getCreatedBy().getPrenom());

        return dto;
    }

    @Override
    public List<ElectionResponseDTO> getAll() {

        return electionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ElectionResponseDTO getById(Long id) {

        Election election = electionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );

        return mapToDTO(election);
    }

    @Override
    public ElectionResponseDTO update(Long id, CreateElectionRequest request) {

        Election election = electionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );

        if(request.getDateFin().isBefore(request.getDateDebut())) {
            throw new RuntimeException(
                    "La date de fin doit être après la date de début"
            );
        }

        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setDateDebut(request.getDateDebut());
        election.setDateFin(request.getDateFin());

        Election saved = electionRepository.save(election);

        return mapToDTO(saved);
    }

    private ElectionResponseDTO mapToDTO(Election election) {

        ElectionResponseDTO dto = new ElectionResponseDTO();

        dto.setId(election.getId());
        dto.setTitle(election.getTitle());
        dto.setDescription(election.getDescription());

        dto.setDateCreation(election.getDateCreation());
        dto.setDateDebut(election.getDateDebut());
        dto.setDateFin(election.getDateFin());

        dto.setStatut(election.getStatut());

        if(election.getCreatedBy() != null) {

            dto.setCreatedByNom(
                    election.getCreatedBy().getNom()
            );

            dto.setCreatedByPrenom(
                    election.getCreatedBy().getPrenom()
            );
        }

        return dto;
    }

    @Override
    public void delete(Long id) {

        if(!electionRepository.existsById(id)) {
            throw new RuntimeException("Election introuvable");
        }

        electionRepository.deleteById(id);
    }
}
