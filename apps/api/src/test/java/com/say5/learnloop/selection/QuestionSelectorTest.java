package com.say5.learnloop.selection;

import static org.assertj.core.api.Assertions.assertThat;

import com.say5.learnloop.engine.EloEngine;
import com.say5.learnloop.selection.QuestionSelector.Candidate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QuestionSelectorTest {

  private static final double TARGET = 0.70;

  // For target 0.70, the ideal difficulty sits below the learner rating.
  // expected = 0.70 -> difficulty = rating - 400*log10(0.70/0.30) ~= rating - 147.
  private List<Candidate> pool() {
    return List.of(
        new Candidate(1, 1000),
        new Candidate(2, 1053), // closest to 0.70 for a 1200 learner
        new Candidate(3, 1200),
        new Candidate(4, 1400));
  }

  @Test
  void picksQuestionClosestToTarget() {
    Optional<Candidate> chosen = QuestionSelector.select(1200, pool(), Set.of(), TARGET);
    assertThat(chosen).isPresent();
    double expected = EloEngine.expectedScore(1200, chosen.get().difficulty());
    // No other eligible candidate is closer to the target.
    for (Candidate c : pool()) {
      double other = EloEngine.expectedScore(1200, c.difficulty());
      assertThat(Math.abs(expected - TARGET)).isLessThanOrEqualTo(Math.abs(other - TARGET));
    }
  }

  @Test
  void excludesCooldownQuestions() {
    Candidate best = QuestionSelector.select(1200, pool(), Set.of(), TARGET).orElseThrow();
    Optional<Candidate> next =
        QuestionSelector.select(1200, pool(), Set.of(best.questionId()), TARGET);
    assertThat(next).isPresent();
    assertThat(next.get().questionId()).isNotEqualTo(best.questionId());
  }

  @Test
  void fallsBackToFullPoolWhenAllOnCooldown() {
    Set<Long> all = Set.of(1L, 2L, 3L, 4L);
    Optional<Candidate> chosen = QuestionSelector.select(1200, pool(), all, TARGET);
    assertThat(chosen).isPresent();
  }

  @Test
  void emptyPoolReturnsEmpty() {
    assertThat(QuestionSelector.select(1200, List.of(), Set.of(), TARGET)).isEmpty();
  }
}
