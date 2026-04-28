package com.amicalestar.backend.repositories;

import com.amicalestar.backend.entities.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReponseRepository extends JpaRepository<Reponse, Long> {
    @Query("""
    SELECT r.choix.id, COUNT(r)
    FROM Reponse r
    WHERE r.question.id = :questionId
    GROUP BY r.choix.id
""")
    List<Object[]> countReponsesByQuestion(Long questionId);
}