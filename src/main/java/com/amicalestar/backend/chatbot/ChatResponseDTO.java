package com.amicalestar.backend.chatbot;

import java.util.List;

public class ChatResponseDTO {

    // === Intention détectée par l’IA ===
    // Exemple : search_event, greeting...
    public String intent;

    // === Nombre de participants ===
    public Integer participants;

    // === Budget utilisateur ===
    public Integer budget;

    // === Type événement ===
    // Exemple : omra, voyage, convention...
    public String type;

    // === Mots-clés détectés ===
    public List<String> keywords;

}