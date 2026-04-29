package com.amicalestar.backend.chatbot;

import com.amicalestar.backend.entities.evenement.Evenement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OllamaService {

    public ChatResponseDTO askAI(String userMessage) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            String prompt = """
You are an intelligent assistant for event recommendation.

Your task is to extract structured data from a user's message.

Extract the following fields:
- budget: number (in dinars) or null
- participants: number or null
- intent: one of [vacation, religious, business, family, relaxation, adventure] or null
- keywords: array of relevant keywords

Guidelines:
- Keywords should be short (hotel, piscine, famille, luxe, enfants)
- Detect intent by meaning
- If not found → null
- Return ONLY JSON
- No explanation

Example:
Input: "je veux un hotel avec piscine pour famille"
Output:
{
  "budget": null,
  "participants": null,
  "intent": "family",
  "keywords": ["hotel", "piscine", "famille"]
}

User message:
""" + userMessage;

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
    public List<Long> rankEvents(String userMessage, List<Evenement> events) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            StringBuilder eventsText = new StringBuilder();

            for (Evenement e : events) {
                eventsText.append("ID: ").append(e.getId()).append("\n")
                        .append("Titre: ").append(e.getTitre()).append("\n")
                        .append("Description: ").append(e.getDescription()).append("\n\n");
            }

            String prompt = """
        You are an intelligent assistant.

        A user is looking for events.

        User request:
        """ + userMessage + """

        Here is a list of events:

        """ + eventsText + """

        Task:
        - Select the most relevant events
        - Return ONLY a JSON array of IDs sorted by relevance
        - Example: [5,2,9]
        - No explanation
        """;

            Map<String, Object> body = new HashMap<>();
            body.put("model", "mistral");
            body.put("prompt", prompt);
            body.put("stream", false);

            String raw = restTemplate.postForObject(
                    "http://localhost:11434/api/generate",
                    body,
                    String.class
            );

            Map<?, ?> result = mapper.readValue(raw, Map.class);
            String json = ((String) result.get("response")).trim();

            return mapper.readValue(json, List.class);

        } catch (Exception e) {
            e.printStackTrace();
            return events.stream()
                    .map(Evenement::getId)
                    .collect(Collectors.toList());
        }
    }
}