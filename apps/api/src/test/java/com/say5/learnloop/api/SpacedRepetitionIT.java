package com.say5.learnloop.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.say5.learnloop.PostgresIntegrationTest;
import com.say5.learnloop.data.ReviewScheduleRepository;
import com.say5.learnloop.domain.ReviewSchedule;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SpacedRepetitionIT extends PostgresIntegrationTest {

  private static final long SKILL = 2L;

  @Autowired PracticeService practice;
  @Autowired ReviewScheduleRepository reviews;

  @Test
  void masteringASkillCreatesAReviewSchedule() {
    long learner = 5L;
    long q = practice.next(learner, SKILL).nextQuestion().questionId();
    // Drive the learner up to MASTERED with a long correct streak.
    for (int i = 0; i < 60; i++) {
      var res = practice.submit(learner, SKILL, q, true);
      q = res.nextQuestion().questionId();
      if (res.masteryLevel() == com.say5.learnloop.domain.MasteryLevel.MASTERED) {
        break;
      }
    }
    var schedule = reviews.findById(new ReviewSchedule.Key(learner, SKILL));
    assertThat(schedule).isPresent();
    // A freshly created schedule is not yet due.
    assertThat(practice.dueForReview(learner)).doesNotContain(SKILL);
  }

  @Test
  void anItemBecomesDueOnceItsIntervalElapses() {
    long learner = 6L;
    // Seed a schedule that is already past due.
    reviews.save(
        new ReviewSchedule(learner, SKILL, 1, 2.5, 1, Instant.now().minus(2, ChronoUnit.HOURS)));
    assertThat(practice.dueForReview(learner)).contains(SKILL);
  }
}
