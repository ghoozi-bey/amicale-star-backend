package com.amicalestar.backend.dto;

import com.amicalestar.backend.enums.TypeQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String text;
    private TypeQuestion type;
    private List<ChoixResponse> choixList;
    private Boolean required;
}