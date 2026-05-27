package com.say5.learnloop.selection;

import static org.assertj.core.api.Assertions.assertThat;

import com.say5.learnloop.engine.EloEngine;
import com.say5.learnloop.selection.QuestionSelector.Candidate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.Size;

/** The selector always returns the eligible candidate whose expected success is nearest target. */
class QuestionSelectorPropertyTest {

  private static final double TARGET = 0.70;

  @Property
  void chosenQuestionIsClosestToTargetAmongEligible(
      @ForAll @DoubleRange(min = 600, max = 2200) double rating,
      @ForAll @Size(min = 1, max = 30) List<@DoubleRange(min = 600, max = 2200) Double> diffs) {
    List<Candidate> pool = new ArrayList<>();
    long id = 1;
    for (double d : diffs) {
      pool.add(new Candidate(id++, d));
    }

    Candidate chosen = QuestionSelector.select(rating, pool, Set.of(), TARGET).orElseThrow();
    double chosenDistance = Math.abs(EloEngine.expectedScore(rating, chosen.difficulty()) - TARGET);

    for (Candidate c : pool) {
      double d = Math.abs(EloEngine.expectedScore(rating, c.difficulty()) - TARGET);
      assertThat(chosenDistance).isLessThanOrEqualTo(d + 1e-12);
    }
  }

  @Property
  void cooldownQuestionsAreSkippedWhenAlternativesExist(
      @ForAll @DoubleRange(min = 600, max = 2200) double rating) {
    List<Candidate> pool =
        List.of(
            new Candidate(1, 1000),
            new Candidate(2, 1100),
            new Candidate(3, 1200),
            new Candidate(4, 1300));
    Candidate best = QuestionSelector.select(rating, pool, Set.of(), TARGET).orElseThrow();
    Candidate next =
        QuestionSelector.select(rating, pool, Set.of(best.questionId()), TARGET).orElseThrow();
    assertThat(next.questionId()).isNotEqualTo(best.questionId());
  }
}
