package com.amicalestar.backend.chatbot;

import com.amicalestar.backend.entities.evenement.Evenement;
import com.amicalestar.backend.enums.StatutEvenement;
import com.amicalestar.backend.repositories.evenement.EvenementRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static java.awt.SystemColor.text;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final OllamaService ollamaService;

    private final EvenementRepository evenementRepository;

    private final Map<String, ChatSession> sessions =
            new HashMap<>();

    public ChatbotController(
            OllamaService ollamaService,
            EvenementRepository evenementRepository
    ) {

        this.ollamaService = ollamaService;

        this.evenementRepository = evenementRepository;
    }

    // === Chatbot endpoint ===
    @PostMapping("/ai")
    public Object chat(
            @RequestBody String message,
            Principal principal
    ) {

        String username =
                principal != null
                        ? principal.getName()
                        : "Utilisateur";

        String cleanMessage =
                message.toLowerCase().trim();

        // === Messages salutation ===
        if (
                cleanMessage.contains("bonjour")
                        || cleanMessage.contains("salut")
                        || cleanMessage.contains("bonsoir")
                        || cleanMessage.contains("cc")
                        || cleanMessage.contains("coucou")
                        || cleanMessage.contains("hey")
                        || cleanMessage.contains("hi")
        ) {

            return Map.of(
                    "type", "text",
                    "message", "Bonjour 👋 Comment puis-je vous aider ?"
            );
        }

        // === Affichage tous les événements ===
        if (
                cleanMessage.contains("tout")
                        && cleanMessage.contains("evenement")
        ) {

            sessions.remove(username);

            List<Evenement> results =
                    evenementRepository.findByStatut(
                            StatutEvenement.ACTIF
                    );

            return Map.of(
                    "type", "events",
                    "events", results.stream().map(e -> {

                        String imageBase64 = null;

                        if (e.getPhoto() != null) {

                            imageBase64 =
                                    "data:image/jpeg;base64," +
                                            Base64.getEncoder()
                                                    .encodeToString(
                                                            e.getPhoto()
                                                    );
                        }

                        return Map.of(
                                "id", e.getId(),
                                "nom", e.getTitre(),
                                "dateDebut", e.getDateDebut(),
                                "prix", e.getPrix(),
                                "imageUrl", imageBase64
                        );

                    }).toList()
            );
        }

        // === Récupération session utilisateur ===
        ChatSession session =
                sessions.getOrDefault(
                        username,
                        new ChatSession()
                );

        // === Analyse IA ===
        ChatResponseDTO dto =
                ollamaService.askAI(cleanMessage);

        // === Normalisation type événement ===
        String normalizedType =
                normalizeType(
                        dto.type,
                        cleanMessage
                );

        Integer participants =
                dto.participants;

        Integer budget =
                dto.budget;

        // Sauvegarde type
        if (
                session.type == null
                        && normalizedType != null
        ) {

            session.type = normalizedType;
        }

        // Sauvegarde participants
        if (
                session.participants == null
                        && participants != null
        ) {

            session.participants = participants;
        }

        // Sauvegarde budget
        if (
                session.budget == null
                        && budget != null
        ) {

            session.budget = budget;
        }

        sessions.put(username, session);

        // === Demande type événement ===
        if (session.type == null) {

            return Map.of(
                    "type", "text",
                    "message", "Quel type d’événement cherchez-vous ? (voyage, omra, convention)"
            );
        }

        // === Cas spécial convention ===
        if ("CONVENTION".equals(session.type)) {

            // Première demande société
            if (session.societe == null) {

                if (cleanMessage.contains("convention")) {

                    return Map.of(
                            "type", "text",
                            "message", "Quelle société cherchez-vous ?"
                    );
                }

                session.societe = cleanMessage;

                sessions.put(username, session);
            }

            List<Evenement> results =
                    evenementRepository.searchConvention(
                            session.societe
                    );

            // Aucun résultat convention
            if (results.isEmpty()) {

                sessions.remove(username);

                return Map.of(
                        "type", "text",
                        "message", "Aucune convention correspondant à cette société n’a été trouvée."
                );
            }

            sessions.remove(username);

            return Map.of(
                    "type", "events",
                    "events", results.stream().map(e -> {

                        String imageBase64 = null;

                        if (e.getPhoto() != null) {

                            imageBase64 =
                                    "data:image/jpeg;base64," +
                                            Base64.getEncoder()
                                                    .encodeToString(
                                                            e.getPhoto()
                                                    );
                        }

                        return Map.of(
                                "id", e.getId(),
                                "nom", e.getTitre(),
                                "dateDebut", e.getDateDebut(),
                                "prix", e.getPrix(),
                                "imageUrl", imageBase64
                        );

                    }).toList()
            );
        }

        // === Cas normal participants ===
        if (session.participants == null) {

            Integer number =
                    extractNumber(cleanMessage);

            if (number != null) {

                session.participants = number;

                sessions.put(username, session);

                return Map.of(
                        "type", "text",
                        "message", "Quel est votre budget approximatif ?"
                );

            } else {

                return Map.of(
                        "type", "text",
                        "message", "Pour combien de personnes ?"
                );
            }
        }

        // === Gestion budget ===
        if (session.budget == null) {

            if (cleanMessage.matches("\\d+")) {

                session.budget =
                        Integer.parseInt(cleanMessage);

                sessions.put(username, session);

            } else {

                return Map.of(
                        "type", "text",
                        "message", "Quel est votre budget approximatif ?"
                );
            }
        }

        // === Recherche avancée événements ===
        List<Evenement> results =
                evenementRepository.searchAdvanced(
                        session.budget,
                        session.participants,
                        session.type,
                        ""
                );

        // Aucun événement trouvé
        if (results.isEmpty()) {

            sessions.remove(username);

            return Map.of(
                    "type", "text",
                    "message", "Aucun événement correspondant n’a été trouvé."
            );
        }

        sessions.remove(username);

        // === Retour événements ===
        return Map.of(
                "type", "events",
                "events", results.stream().map(e -> {

                    String imageBase64 = null;

                    if (e.getPhoto() != null) {

                        imageBase64 =
                                "data:image/jpeg;base64," +
                                        Base64.getEncoder()
                                                .encodeToString(
                                                        e.getPhoto()
                                                );
                    }

                    return Map.of(
                            "id", e.getId(),
                            "nom", e.getTitre(),
                            "dateDebut", e.getDateDebut(),
                            "prix", e.getPrix(),
                            "imageUrl", imageBase64
                    );

                }).toList()
        );
    }

    // === Normalisation type événement ===
    private String normalizeType(
            String type,
            String message
    ) {

        if (
                type == null
                        || type.equalsIgnoreCase("null")
        ) {

            return null;
        }

        String text =
                type + " " + message;

        text = text.toLowerCase();

        // OMRA / HAJ
        if (
                text.contains("omra")
                        || text.contains("hajj")
                        || text.contains("haj")
        ) {

            return "OMRA & HAJ";
        }

        // VOYAGE
        if (
                text.contains("voyage")
                        || text.contains("trip")
                        || text.contains("travel")
                        || text.contains("hotel")
                        || text.contains("voyage dans le sud")
        ) {

            return "VOYAGE";
        }

        // CONVENTION
        if (
                text.contains("convention")
                        || text.contains("ooredoo")
                        || text.contains("tunisieTelecon")
                        || text.contains("orange")
                        || text.contains("OOREDOO")
                        || text.contains("TunisieTélécom")
                        || text.contains("ORANGE")
        ) {

            return "CONVENTION";
        }

        return null;
    }

    // === Extraction nombre participants ===
    private Integer extractNumber(String text) {

        text = text.toLowerCase();

        // Extraction chiffres
        String digits =
                text.replaceAll("\\D", "");

        if (!digits.isEmpty()) {

            return Integer.parseInt(digits);
        }

        // Extraction mots
        if (text.contains("un") || text.contains("une")) return 1;
        if (text.contains("deux")) return 2;
        if (text.contains("trois")) return 3;
        if (text.contains("quatre")) return 4;
        if (text.contains("cinq")) return 5;
        if (text.contains("six")) return 6;
        if (text.contains("sept")) return 7;
        if (text.contains("huit")) return 8;
        if (text.contains("neuf")) return 9;
        if (text.contains("dix")) return 10;

        return null;
    }

}