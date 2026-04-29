package com.amicalestar.backend.chatbot;

import java.util.List;

public class ChatSession {

    public String step; // GREETING, INTENT, DETAILS, RECOMMENDATION
    public String intent;
    public Integer budget;
    public Integer participants;
    public String location;
    public List<String> keywords;
}