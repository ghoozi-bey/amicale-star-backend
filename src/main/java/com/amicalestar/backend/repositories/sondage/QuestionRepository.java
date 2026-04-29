package com.amicalestar.backend.repositories.sondage;

import com.amicalestar.backend.entities.sondage.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}