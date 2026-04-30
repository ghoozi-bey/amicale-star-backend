package com.amicalestar.backend.chatbot;

import java.util.List;

public class ChatResponseDTO {

    public String intent;        // search_event, greeting...
    public Integer participants; // nombre de personnes
    public Integer budget;      // budget
    public String type;         // omra, voyage...
    public List<String> keywords;


}