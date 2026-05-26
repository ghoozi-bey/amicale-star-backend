package com.amicalestar.backend.chatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // === URL API Ollama ===
    private final String URL = "http://localhost:11434/api/generate";

    // === Analyse IA du message utilisateur ===
    public ChatResponseDTO askAI(String message) {

        try {

            // === Prompt envoyé à l'IA ===
            String prompt = """
            You are an AI that extracts structured data.

            STRICT RULES:
            - Respond ONLY with valid JSON
            - No text before or after JSON
            - No explanation

            Format EXACTLY:
            {
              "type": "voyage | omra | convention | null",
              "participants": number or null,
              "budget": number or null
            }

            Examples:

            User: je veux une omra pour 2 personnes avec 3000
            {
              "type": "omra",
              "participants": 2,
              "budget": 3000
            }

            User: """ + message;

            // === Construction requête Ollama ===
            Map<String, Object> request = new HashMap<>();

            request.put("model", "llama3");
            request.put("prompt", prompt);
            request.put("stream", false);

            // === Appel API Ollama ===
            Map response =
                    restTemplate.postForObject(
                            URL,
                            request,
                            Map.class
                    );

            // === Réponse brute IA ===
            String result =
                    (String) response.get("response");

            System.out.println(
                    "RAW IA: " + result
            );

            // === Extraction JSON ===
            String json =
                    extractJson(result);

            // === Conversion JSON -> DTO ===
            return objectMapper.readValue(
                    json,
                    ChatResponseDTO.class
            );

        } catch (Exception e) {

            e.printStackTrace();

            // === Fallback sécurisé ===
            return new ChatResponseDTO();
        }
    }

    // === Appel simple Ollama ===
    public String ask(String message) {

        Map<String, Object> request =
                new HashMap<>();

        request.put("model", "llama3");
        request.put("prompt", message);
        request.put("stream", false);

        Map response =
                restTemplate.postForObject(
                        URL,
                        request,
                        Map.class
                );

        return (String) response.get("response");
    }

    // === Extraction JSON depuis réponse IA ===
    private String extractJson(String text) {

        if (text == null)
            return "{}";

        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");

        if (start != -1 &&
                end != -1 &&
                end > start) {

            return text.substring(
                    start,
                    end + 1
            );
        }

        return "{}";
    }
}