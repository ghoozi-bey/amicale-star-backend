package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.Participation.ParticipationDTO;
import com.amicalestar.backend.dto.Participation.ReponseDTO;
import com.amicalestar.backend.dto.stats.ChoixStatsDTO;
import com.amicalestar.backend.dto.stats.QuestionStatsDTO;
import com.amicalestar.backend.dto.stats.SondageStatsDTO;
import com.amicalestar.backend.entities.Choix;
import com.amicalestar.backend.entities.Participation;
import com.amicalestar.backend.entities.Question;
import com.amicalestar.backend.entities.Sondage;
import com.amicalestar.backend.repositories.ParticipationRepository;
import com.amicalestar.backend.repositories.ReponseRepository;
import com.amicalestar.backend.repositories.SondageRepository;
import com.amicalestar.backend.services.SondageStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SondageStatsServiceImpl implements SondageStatsService {

    private final SondageRepository sondageRepository;
    private final ParticipationRepository participationRepository;
    private final ReponseRepository reponseRepository;

    @Override
    public SondageStatsDTO getStats(Long sondageId) {

        Sondage sondage = sondageRepository.findById(sondageId)
                .orElseThrow(() -> new RuntimeException("Sondage not found"));

        int totalParticipants = participationRepository.countBySondageId(sondageId);

        List<QuestionStatsDTO> questionStatsList = new ArrayList<>();

        for (Question q : sondage.getQuestions()) {

            QuestionStatsDTO qStats = new QuestionStatsDTO();
            qStats.setQuestionId(q.getId());
            qStats.setQuestionText(q.getText());

            List<Object[]> results = reponseRepository.countReponsesByQuestion(q.getId());

            Map<Long, Integer> countsMap = new HashMap<>();
            for (Object[] obj : results) {
                Long choixId = (Long) obj[0];
                int count = ((Long) obj[1]).intValue();
                countsMap.put(choixId, count);
            }

            List<ChoixStatsDTO> choixStatsList = new ArrayList<>();

            for (Choix c : q.getChoixList()) {

                int count = countsMap.getOrDefault(c.getId(), 0);

                double percentage = totalParticipants == 0
                        ? 0
                        : (count * 100.0) / totalParticipants;

                ChoixStatsDTO cStats = new ChoixStatsDTO();
                cStats.setChoixId(c.getId());
                cStats.setLabel(c.getLabel());
                cStats.setCount(count);
                cStats.setPercentage(
                        Math.round(percentage * 100.0) / 100.0
                );

                choixStatsList.add(cStats);
            }

            qStats.setChoix(choixStatsList);
            questionStatsList.add(qStats);
        }

        SondageStatsDTO dto = new SondageStatsDTO();
        dto.setTotalParticipants(totalParticipants);
        dto.setQuestions(questionStatsList);

        return dto;
    }

    @Override
    public List<ParticipationDTO> getParticipations(Long sondageId) {

        List<Participation> participations =
                participationRepository.findBySondageId(sondageId);

        return participations.stream().map(p -> {

            ParticipationDTO dto = new ParticipationDTO();
            dto.setNom(p.getAdherent().getNom());
            dto.setEmail(p.getAdherent().getEmail());

            List<ReponseDTO> reponses = p.getReponses().stream().map(r -> {

                ReponseDTO rDto = new ReponseDTO();
                rDto.setQuestion(r.getQuestion().getText());

                if (r.getChoix() != null) {
                    rDto.setReponse(r.getChoix().getLabel());
                } else {
                    rDto.setReponse(r.getTexte());
                }

                return rDto;
            }).toList();

            dto.setReponses(reponses);

            return dto;

        }).toList();
    }
}
