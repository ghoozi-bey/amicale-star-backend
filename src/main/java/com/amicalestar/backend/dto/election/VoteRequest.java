package com.amicalestar.backend.dto.election;

import lombok.Data;

import java.util.List;

@Data
public class VoteRequest {

    private Long electionId;

    private List<Long> candidatIds;

}