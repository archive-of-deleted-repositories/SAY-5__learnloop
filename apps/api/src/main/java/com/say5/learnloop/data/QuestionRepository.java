package com.say5.learnloop.data;

import com.say5.learnloop.domain.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
  List<Question> findBySkillId(Long skillId);
}
