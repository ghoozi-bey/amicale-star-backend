package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.election.VoteRequest;
import com.amicalestar.backend.entities.Adherent;

public interface VoteService {

    // === Enregistrement d’un vote ===
    void voter(
            VoteRequest request,
            Adherent currentUser
    );

    // === Vérification de participation au vote ===
    boolean hasVoted(
            Long electionId,
            Adherent currentUser
    );

}