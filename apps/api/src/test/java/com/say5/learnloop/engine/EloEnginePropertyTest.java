package com.say5.learnloop.engine;

import static org.assertj.core.api.Assertions.assertThat;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;

/** Invariants the Elo engine must hold for any rating, difficulty and K within sane ranges. */
class EloEnginePropertyTest {

  @Property
  void expectedScoreIsAlwaysInOpenUnitInterval(
      @ForAll @DoubleRange(min = 0, max = 4000) double rating,
      @ForAll @DoubleRange(min = 0, max = 4000) double difficulty) {
    double expected = EloEngine.expectedScore(rating, difficulty);
    assertThat(expected).isGreaterThan(0.0).isLessThan(1.0);
  }

  @Property
  void correctAnswerNeverDecreasesLearnerRating(
      @ForAll @DoubleRange(min = 0, max = 4000) double rating,
      @ForAll @DoubleRange(min = 0, max = 4000) double difficulty,
      @ForAll @DoubleRange(min = 1, max = 64) double k) {
    EloUpdate u = EloEngine.update(rating, difficulty, true, k);
    assertThat(u.learnerRating()).isGreaterThanOrEqualTo(rating);
  }

  @Property
  void wrongAnswerNeverIncreasesLearnerRating(
      @ForAll @DoubleRange(min = 0, max = 4000) double rating,
      @ForAll @DoubleRange(min = 0, max = 4000) double difficulty,
      @ForAll @DoubleRange(min = 1, max = 64) double k) {
    EloUpdate u = EloEngine.update(rating, difficulty, false, k);
    assertThat(u.learnerRating()).isLessThanOrEqualTo(rating);
  }

  @Property
  void learnerGainEqualsQuestionLoss(
      @ForAll @DoubleRange(min = 0, max = 4000) double rating,
      @ForAll @DoubleRange(min = 0, max = 4000) double difficulty,
      @ForAll @DoubleRange(min = 1, max = 64) double k,
      @ForAll boolean correct) {
    EloUpdate u = EloEngine.update(rating, difficulty, correct, k);
    double learnerDelta = u.learnerRating() - rating;
    double questionDelta = u.questionDifficulty() - difficulty;
    // Symmetry: a learner win is a question loss of equal magnitude.
    assertThat(learnerDelta)
        .isCloseTo(-questionDelta, org.assertj.core.api.Assertions.within(1e-9));
  }

  @Property
  void ratingsStayBoundedByK(
      @ForAll @DoubleRange(min = 0, max = 4000) double rating,
      @ForAll @DoubleRange(min = 0, max = 4000) double difficulty,
      @ForAll @DoubleRange(min = 1, max = 64) double k,
      @ForAll boolean correct) {
    EloUpdate u = EloEngine.update(rating, difficulty, correct, k);
    // A single update can move a rating by at most K.
    assertThat(Math.abs(u.learnerRating() - rating)).isLessThanOrEqualTo(k + 1e-9);
  }

  @Property
  void higherRatedLearnerHasHigherExpectedScore(
      @ForAll @DoubleRange(min = 0, max = 2000) double low,
      @ForAll @DoubleRange(min = 0, max = 2000) double gap,
      @ForAll @DoubleRange(min = 0, max = 4000) double difficulty) {
    double high = low + gap + 1.0;
    assertThat(EloEngine.expectedScore(high, difficulty))
        .isGreaterThan(EloEngine.expectedScore(low, difficulty));
  }
}
