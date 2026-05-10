package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.election.VoteRequest;
import com.amicalestar.backend.entities.Adherent;

public interface VoteService {

    void voter(
            VoteRequest request,
            Adherent currentUser
    );

    boolean hasVoted(
            Long electionId,
            Adherent currentUser
    );

}