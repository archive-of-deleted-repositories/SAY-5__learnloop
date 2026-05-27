package com.say5.learnloop.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.say5.learnloop.PostgresIntegrationTest;
import com.say5.learnloop.api.Dtos.NextQuestionResponse;
import com.say5.learnloop.api.Dtos.StudentDashboard;
import com.say5.learnloop.api.Dtos.SubmitAnswerResponse;
import com.say5.learnloop.api.Dtos.TeacherDashboard;
import com.say5.learnloop.domain.MasteryLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PracticeApiIT extends PostgresIntegrationTest {

  private static final long STUDENT = 1L;
  private static final long SKILL = 1L;

  @Autowired PracticeService practice;
  @Autowired DashboardService dashboards;

  @Test
  void correctStreakRaisesRatingAndAdvancesMastery() {
    NextQuestionResponse start = practice.next(STUDENT, SKILL);
    double startRating = start.learnerRating();
    assertThat(start.nextQuestion()).isNotNull();

    SubmitAnswerResponse last = null;
    long questionId = start.nextQuestion().questionId();
    for (int i = 0; i < 30; i++) {
      last = practice.submit(STUDENT, SKILL, questionId, true);
      questionId = last.nextQuestion().questionId();
    }

    assertThat(last).isNotNull();
    assertThat(last.learnerRating()).isGreaterThan(startRating);
    // From an all-correct streak the learner should climb out of NOVICE.
    assertThat(last.masteryLevel().ordinal()).isGreaterThan(MasteryLevel.NOVICE.ordinal());
  }

  @Test
  void nextQuestionTargetsSeventyPercent() {
    // Seed a fresh skill state by answering a few; the selector should keep expected near 0.70.
    practice.submit(2L, SKILL, practice.next(2L, SKILL).nextQuestion().questionId(), true);
    NextQuestionResponse next = practice.next(2L, SKILL);
    double rating = next.learnerRating();
    double diff = next.nextQuestion().difficulty();
    double expected = com.say5.learnloop.engine.EloEngine.expectedScore(rating, diff);
    assertThat(expected).isBetween(0.5, 0.9);
  }

  @Test
  void studentDashboardReflectsProgress() {
    long student = 3L;
    long q = practice.next(student, SKILL).nextQuestion().questionId();
    for (int i = 0; i < 12; i++) {
      q = practice.submit(student, SKILL, q, true).nextQuestion().questionId();
    }
    StudentDashboard dash = dashboards.student(student);
    assertThat(dash.skills()).isNotEmpty();
    assertThat(dash.recentTrend()).isNotEmpty();
    var skillOne =
        dash.skills().stream().filter(s -> s.skillId() == SKILL).findFirst().orElseThrow();
    assertThat(skillOne.rating()).isGreaterThan(1200.0);
  }

  @Test
  void teacherDashboardListsRosterAndDistributions() {
    long student = 4L;
    long q = practice.next(student, SKILL).nextQuestion().questionId();
    for (int i = 0; i < 10; i++) {
      q = practice.submit(student, SKILL, q, true).nextQuestion().questionId();
    }
    TeacherDashboard dash = dashboards.teacher("period-1");
    assertThat(dash.roster()).hasSize(6);
    assertThat(dash.distributions()).hasSize(3);
    assertThat(dash.weakestSkills()).hasSize(3);
    // weakest list is sorted ascending by median score
    assertThat(dash.weakestSkills().get(0).medianScore())
        .isLessThanOrEqualTo(dash.weakestSkills().get(2).medianScore());
  }
}
