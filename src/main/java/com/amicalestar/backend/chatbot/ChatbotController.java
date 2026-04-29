package com.amicalestar.backend.chatbot;

import com.amicalestar.backend.entities.evenement.Evenement;
import com.amicalestar.backend.repositories.evenement.EvenementRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private final OllamaService ollamaService;
    private final EvenementRepository evenementRepository;

    public ChatbotController(OllamaService ollamaService,
                             EvenementRepository evenementRepository) {
        this.ollamaService = ollamaService;
        this.evenementRepository = evenementRepository;
    }

    @PostMapping("/ai")
    public Object chat(@RequestBody String message, Principal principal) {

        String username = principal != null ? principal.getName() : "Utilisateur";
        String msg = message.toLowerCase();

        // 🔥 récupérer session
        ChatSession session = sessions.getOrDefault(username, new ChatSession());

        // 🧠 1. GREETING
        if (msg.contains("bonjour") || msg.contains("salut")) {
            return "Bonjour " + username + " 👋\nComment puis-je vous aider ?\n" +
                    "- Voir événements\n" +
                    "- Faire une inscription\n" +
                    "- Trouver un événement adapté";
        }

        // 🧠 2. INSCRIPTION
        if (msg.contains("inscription")) {
            return "Vous pouvez consulter et gérer vos inscriptions depuis votre dashboard.";
        }

        // 🧠 3. DASHBOARD
        if (msg.contains("dashboard")) {
            return "Accédez à votre dashboard pour suivre vos inscriptions, paiements et événements.";
        }

        // 🧠 4. IA extraction
        ChatResponseDTO dto = ollamaService.askAI(message);

        if (dto != null) {
            if (dto.intent != null) session.intent = dto.intent;
            if (dto.budget != null) session.budget = dto.budget;
            if (dto.participants != null) session.participants = dto.participants;
            if (dto.keywords != null) session.keywords = dto.keywords;
        }

        sessions.put(username, session);

        // 🧠 5. QUESTIONS PROGRESSIVES

        if (session.intent == null) {
            return "Quel type d’événement cherchez-vous ? (voyage, omra, convention...)";
        }

        if (session.participants == null) {
            return "Pour combien de personnes ?";
        }

        if (session.budget == null) {
            return "Quel est votre budget approximatif ?";
        }

        // 🧠 6. RECOMMANDATION

        String type = mapIntent(session.intent);

        List<Evenement> results = new ArrayList<>();

        if (session.keywords != null && !session.keywords.isEmpty()) {
            for (String keyword : session.keywords) {
                results.addAll(
                        evenementRepository.searchAdvanced(
                                session.budget,
                                session.participants,
                                type,
                                keyword
                        )
                );
            }
        } else {
            results = evenementRepository.searchAdvanced(
                    session.budget,
                    session.participants,
                    type,
                    ""
            );
        }

        // 🔥 supprimer doublons
        List<Evenement> unique = results.stream()
                .distinct()
                .collect(Collectors.toList());

        // 🔥 ranking IA
        List<Long> rankedIds = ollamaService.rankEvents(message, unique);

        List<Evenement> finalResults = rankedIds.stream()
                .map(id -> unique.stream()
                        .filter(e -> e.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 🧠 7. PAS DE RESULTAT
        if (finalResults.isEmpty()) {
            return "Je n’ai pas trouvé d’événements 😕\n" +
                    "Essayez de modifier votre budget ou votre type.";
        }

        // 🧠 8. RESET SESSION (IMPORTANT)
        sessions.remove(username);

        // 🧠 9. RÉPONSE FINALE
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Parfait " + username + " 👌 Voici les meilleurs événements pour vous 👇");
        response.put("events", finalResults);

        return response;
    }

    private String mapIntent(String intent) {

        if (intent == null) return null;

        intent = intent.toLowerCase();

        if (intent.equals("vacation") || intent.equals("relaxation") ||
                intent.equals("adventure") || intent.equals("family")) {
            return "voyage";
        } else if (intent.equals("religious")) {
            return "omra";
        } else if (intent.equals("business")) {
            return "convention";
        }

        return null;
    }
    private Map<String, ChatSession> sessions = new HashMap<>();
}
