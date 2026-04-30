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

    private final String URL = "http://localhost:11434/api/generate";


    public ChatResponseDTO askAI(String message) {
        

        try {

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

            Map<String, Object> request = new HashMap<>();
            request.put("model", "llama3");
            request.put("prompt", prompt);
            request.put("stream", false);

            Map response = restTemplate.postForObject(URL, request, Map.class);

            String result = (String) response.get("response");

            System.out.println("RAW IA: " + result); // 🔥 DEBUG

            String json = extractJson(result);

            return objectMapper.readValue(json, ChatResponseDTO.class);

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponseDTO(); // 🔥 fallback safe
        }
    }

    public String ask(String message) {

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama3");
        request.put("prompt", message);
        request.put("stream", false);

        Map response = restTemplate.postForObject(URL, request, Map.class);

        return (String) response.get("response");
    }

    private String extractJson(String text) {

        if (text == null) return "{}";

        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");

        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }

        return "{}";
    }
}