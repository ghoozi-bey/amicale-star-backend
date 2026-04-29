package com.amicalestar.backend.chatbot;

import com.amicalestar.backend.entities.evenement.Evenement;
import com.amicalestar.backend.repositories.evenement.EvenementRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Evenement> chat(@RequestBody String message) {

        ChatResponseDTO dto = ollamaService.askAI(message);

        System.out.println("Budget: " + dto.budget);
        System.out.println("Participants: " + dto.participants);
        System.out.println("Type: " + dto.type);

        return evenementRepository.findRecommended(
                dto.budget,
                dto.participants,
                dto.type
        );
    }
}