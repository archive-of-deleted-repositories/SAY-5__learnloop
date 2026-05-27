package com.say5.learnloop.mastery;

import com.say5.learnloop.domain.MasteryLevel;
import java.util.List;

/**
 * Derives a mastery bucket from a learner's rating relative to a skill's question-pool difficulty
 * distribution. Harder pools require a higher rating to reach the same bucket.
 *
 * <p>The score is the fraction of the pool the learner is expected to answer correctly, smoothed by
 * the Elo expected-score curve and averaged over the pool. Bands:
 *
 * <pre>
 *   score &lt; 0.25            -&gt; NOVICE
 *   0.25 &lt;= score &lt; 0.50    -&gt; DEVELOPING
 *   0.50 &lt;= score &lt; 0.80    -&gt; PROFICIENT
 *   score &gt;= 0.80           -&gt; MASTERED
 * </pre>
 */
public final class MasteryModel {

  private static final double SCALE = 400.0;
  static final double DEVELOPING_MIN = 0.25;
  static final double PROFICIENT_MIN = 0.50;
  static final double MASTERED_MIN = 0.80;

  private MasteryModel() {}

  /** Mean expected success of {@code rating} across the pool's difficulties, in [0, 1]. */
  public static double score(double rating, List<Double> poolDifficulties) {
    if (poolDifficulties == null || poolDifficulties.isEmpty()) {
      return 0.0;
    }
    double sum = 0.0;
    for (double difficulty : poolDifficulties) {
      sum += 1.0 / (1.0 + Math.pow(10.0, (difficulty - rating) / SCALE));
    }
    return sum / poolDifficulties.size();
  }

  /** Bucket a raw score into a mastery level. */
  public static MasteryLevel bucket(double score) {
    if (score >= MASTERED_MIN) {
      return MasteryLevel.MASTERED;
    }
    if (score >= PROFICIENT_MIN) {
      return MasteryLevel.PROFICIENT;
    }
    if (score >= DEVELOPING_MIN) {
      return MasteryLevel.DEVELOPING;
    }
    return MasteryLevel.NOVICE;
  }

  /** Convenience: derive level directly from rating and pool. */
  public static MasteryLevel level(double rating, List<Double> poolDifficulties) {
    return bucket(score(rating, poolDifficulties));
  }
}
