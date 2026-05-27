package com.say5.learnloop.engine;

/**
 * Pure Elo-style rating engine. No I/O, no state. Given a learner rating, a question difficulty and
 * a K-factor it computes the expected score and the post-answer ratings.
 *
 * <p>expected = 1 / (1 + 10^((difficulty - rating) / 400)) delta = K * (actual - expected) The
 * learner gains exactly what the question loses (zero-sum symmetry).
 */
public final class EloEngine {

  private static final double SCALE = 400.0;

  private EloEngine() {}

  /** Probability the learner answers a question of {@code difficulty} correctly, in (0, 1). */
  public static double expectedScore(double learnerRating, double difficulty) {
    return 1.0 / (1.0 + Math.pow(10.0, (difficulty - learnerRating) / SCALE));
  }

  /**
   * Apply one answer. {@code correct} maps to an actual score of 1.0, otherwise 0.0.
   *
   * @return the updated learner rating and question difficulty.
   */
  public static EloUpdate update(
      double learnerRating, double difficulty, boolean correct, double kFactor) {
    double expected = expectedScore(learnerRating, difficulty);
    double actual = correct ? 1.0 : 0.0;
    double delta = kFactor * (actual - expected);
    return new EloUpdate(learnerRating + delta, difficulty - delta, expected, delta);
  }
}
