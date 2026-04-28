package com.amicalestar.backend.chatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    public ChatResponseDTO askAI(String userMessage) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            String prompt = """
            You are an assistant that extracts structured data.

            Extract:
            - budget (number)
            - participants (number)
            - type (voyage, omra, hajj, convention)

            Rules:
            - relax, aventure, luxe → voyage
            - religieux → omra
            - business → convention
            - Return ONLY JSON

            Message: """ + userMessage;

            Map<String, Object> body = new HashMap<>();
            body.put("model", "mistral");
            body.put("prompt", prompt);
            body.put("stream", false);

            String rawResponse = restTemplate.postForObject(
                    "http://localhost:11434/api/generate",
                    body,
                    String.class
            );

            Map<?, ?> result = mapper.readValue(rawResponse, Map.class);
            String json = (String) result.get("response");

            return mapper.readValue(json, ChatResponseDTO.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}