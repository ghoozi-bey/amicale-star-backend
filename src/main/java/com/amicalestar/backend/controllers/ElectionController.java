package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.Election.CreateElectionRequest;
import com.amicalestar.backend.entities.Election;
import com.amicalestar.backend.services.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    // CREATE
    @PostMapping
    public Election createElection(
            @RequestBody CreateElectionRequest request,
            @RequestParam String createdById
    ) {

        return electionService.create(request, createdById);
    }

    // GET ALL
    @GetMapping
    public List<Election> getAllElections() {

        return electionService.getAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public Election getElectionById(
            @PathVariable Long id
    ) {

        return electionService.getById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Election updateElection(
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
}