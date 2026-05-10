package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.election.VoteRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.services.interfaces.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final AdherentRepository adherentRepository;

    @PostMapping
    public ResponseEntity<?> voter(
            @RequestBody VoteRequest request,
            Authentication authentication
    ) {

        User userDetails =
                (User) authentication.getPrincipal();

        String email = userDetails.getUsername();

        Adherent currentUser =
                adherentRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Utilisateur introuvable"
                                ));

        voteService.voter(request, currentUser);

        return ResponseEntity.ok(
                "Vote enregistré avec succès"
        );
    }

    @GetMapping("/me/{electionId}")
    public ResponseEntity<?> hasVoted(
            @PathVariable Long electionId,
            Authentication authentication
    ) {

        User userDetails =
                (User) authentication.getPrincipal();

        String email =
                userDetails.getUsername();

        Adherent currentUser =
                adherentRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Utilisateur introuvable"
                                ));

        boolean hasVoted =
                voteService.hasVoted(
                        electionId,
                        currentUser
                );

        return ResponseEntity.ok(hasVoted);
    }
}