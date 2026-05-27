package com.say5.learnloop.selection;

import com.say5.learnloop.engine.EloEngine;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Selects the next question for a learner. Among questions not on cooldown, picks the one whose
 * expected success is closest to the target (the desirable-difficulty sweet spot). Pure given
 * (rating, pool, recently-seen ids).
 */
public final class QuestionSelector {

  private QuestionSelector() {}

  /**
   * @param learnerRating the learner's current rating in the skill
   * @param pool candidate questions (id + difficulty), assumed non-empty
   * @param recentlySeen ids seen within the cooldown window, excluded first
   * @param target target expected success, e.g. 0.70
   * @return the chosen candidate, or empty if the pool is empty
   */
  public static Optional<Candidate> select(
      double learnerRating, List<Candidate> pool, Set<Long> recentlySeen, double target) {
    if (pool == null || pool.isEmpty()) {
      return Optional.empty();
    }
    Comparator<Candidate> byDistanceToTarget =
        Comparator.comparingDouble(c -> distance(learnerRating, c, target));

    List<Candidate> eligible =
        pool.stream().filter(c -> !recentlySeen.contains(c.questionId())).toList();

    // If everything is on cooldown, fall back to the full pool.
    List<Candidate> searchSpace = eligible.isEmpty() ? pool : eligible;
    return searchSpace.stream().min(byDistanceToTarget);
  }

  static double distance(double learnerRating, Candidate c, double target) {
    double expected = EloEngine.expectedScore(learnerRating, c.difficulty());
    return Math.abs(expected - target);
  }

  /** A selectable question. */
  public record Candidate(long questionId, double difficulty) {}
}
