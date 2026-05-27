package com.say5.learnloop.data;

import com.say5.learnloop.domain.LearnerSkillRating;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerSkillRatingRepository
    extends JpaRepository<LearnerSkillRating, LearnerSkillRating.Key> {
  List<LearnerSkillRating> findByLearnerId(Long learnerId);

  List<LearnerSkillRating> findBySkillId(Long skillId);
}
