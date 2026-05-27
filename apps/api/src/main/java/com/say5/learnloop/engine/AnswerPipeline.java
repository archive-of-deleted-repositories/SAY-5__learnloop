package com.say5.learnloop.engine;

import com.say5.learnloop.domain.MasteryLevel;
import com.say5.learnloop.mastery.MasteryModel;
import com.say5.learnloop.selection.QuestionSelector;
import com.say5.learnloop.selection.QuestionSelector.Candidate;
import java.util.List;
import java.util.Set;

/**
 * Pure, allocation-light composition of the three hot-path stages: Elo update, mastery recompute
 * and next-question selection. Used by the service layer and by the throughput benchmark so the
 * benchmark measures the same code that runs in production.
 */
public final class AnswerPipeline {

  private AnswerPipeline() {}

  /** Apply one answer and return the new learner rating. */
  public static EloUpdate applyAnswer(
      double learnerRating, double difficulty, boolean correct, double kFactor) {
    return EloEngine.update(learnerRating, difficulty, correct, kFactor);
  }

  /** Recompute the mastery bucket for a learner against a skill pool. */
  public static MasteryLevel recomputeMastery(double learnerRating, List<Double> poolDifficulties) {
    return MasteryModel.bucket(MasteryModel.score(learnerRating, poolDifficulties));
  }

  /** Select the next question id targeting the desired success rate. */
  public static long selectNext(
      double learnerRating, List<Candidate> pool, Set<Long> recent, double target) {
    return QuestionSelector.select(learnerRating, pool, recent, target)
        .map(Candidate::questionId)
        .orElse(-1L);
  }
}
