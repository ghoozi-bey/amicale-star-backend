package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.election.CreateElectionRequest;
import com.amicalestar.backend.dto.election.ElectionResponseDTO;
import com.amicalestar.backend.entities.election.Election;
import com.amicalestar.backend.services.interfaces.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    // CREATE
    @PostMapping
    public ElectionResponseDTO createElection(
            @RequestBody CreateElectionRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return electionService.create(request, email);
    }

    // GET ALL
    @GetMapping
    public List<ElectionResponseDTO> getAllElections() {

        return electionService.getAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ElectionResponseDTO getElectionById(
            @PathVariable Long id
    ) {

        return electionService.getById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ElectionResponseDTO updateElection(
            @PathVariable Long id,
            @RequestBody CreateElectionRequest request
    ) {

        return electionService.update(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteElection(
            @PathVariable Long id
    ) {

        electionService.delete(id);
    }

    // PUBLISH
    @PutMapping("/{id}/publish")
    public void publish(
            @PathVariable Long id
    ) {

        electionService.publish(id);
    }

    // UNPUBLISH
    @PutMapping("/{id}/unpublish")
    public void unpublish(
            @PathVariable Long id
    ) {

        electionService.unpublish(id);
    }
}