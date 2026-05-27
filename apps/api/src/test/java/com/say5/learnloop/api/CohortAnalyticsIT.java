package com.say5.learnloop.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.say5.learnloop.PostgresIntegrationTest;
import com.say5.learnloop.api.Dtos.CohortAnalytics;
import com.say5.learnloop.api.Dtos.WeakSkillReport;
import com.say5.learnloop.data.LearnerRepository;
import com.say5.learnloop.domain.Learner;
import com.say5.learnloop.domain.Role;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CohortAnalyticsIT extends PostgresIntegrationTest {

  private static final long STRUGGLE_SKILL = 3L; // Geometry
  private static final long STRONG_SKILL = 1L; // Fractions

  @Autowired LearnerRepository learners;
  @Autowired PracticeService practice;
  @Autowired CohortAnalyticsService cohort;

  @Test
  void theSkillEveryoneStrugglesWithRanksWorstWithDrillDown() {
    String klass = "cohort-it";
    // A small class of three students, isolated from the seeded period-1.
    List<Long> ids = List.of(901L, 902L, 903L);
    for (long id : ids) {
      learners.save(new Learner(id, "S" + id, Role.STUDENT, klass));
    }

    for (long id : ids) {
      // Strong on Fractions: answer correctly.
      long q = practice.next(id, STRONG_SKILL).nextQuestion().questionId();
      for (int i = 0; i < 10; i++) {
        q = practice.submit(id, STRONG_SKILL, q, true).nextQuestion().questionId();
      }
      // Struggle on Geometry: answer everything wrong.
      long qg = practice.next(id, STRUGGLE_SKILL).nextQuestion().questionId();
      for (int i = 0; i < 12; i++) {
        qg = practice.submit(id, STRUGGLE_SKILL, qg, false).nextQuestion().questionId();
      }
    }

    CohortAnalytics analytics = cohort.forClass(klass);
    assertThat(analytics.rankedSkills()).isNotEmpty();

    // The struggle skill must rank worst (first, since ranked worst-first).
    WeakSkillReport worst = analytics.rankedSkills().get(0);
    assertThat(worst.skillId()).isEqualTo(STRUGGLE_SKILL);
    assertThat(worst.failureRate()).isGreaterThan(0.8);

    // The strong skill ranks better than the struggle skill.
    WeakSkillReport fractions =
        analytics.rankedSkills().stream()
            .filter(r -> r.skillId() == STRONG_SKILL)
            .findFirst()
            .orElseThrow();
    assertThat(worst.struggleScore()).isGreaterThan(fractions.struggleScore());

    // Drill-down surfaces the hardest questions for the struggle skill.
    assertThat(worst.hardestQuestions()).isNotEmpty();
    assertThat(worst.hardestQuestions().get(0).successRate())
        .isLessThanOrEqualTo(
            worst.hardestQuestions().get(worst.hardestQuestions().size() - 1).successRate());
    // Everyone got these wrong, so the hardest question has a 0% class success rate.
    assertThat(worst.hardestQuestions().get(0).successRate()).isEqualTo(0.0);
  }
}
