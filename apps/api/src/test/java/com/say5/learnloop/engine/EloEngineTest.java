package com.say5.learnloop.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

class EloEngineTest {

  private static final double K = 32.0;

  @Test
  void equalRatingsGiveHalfExpected() {
    assertThat(EloEngine.expectedScore(1200, 1200)).isEqualTo(0.5);
  }

  @Test
  void fourHundredPointEdgeIsTenToOne() {
    // A learner 400 points above the question is expected to win ~10:11.
    assertThat(EloEngine.expectedScore(1600, 1200)).isCloseTo(0.90909, within(1e-4));
    // The reverse: 400 points below the question.
    assertThat(EloEngine.expectedScore(1200, 1600)).isCloseTo(0.09091, within(1e-4));
  }

  @Test
  void correctAnswerAtEvenMatchMovesBySixteen() {
    EloUpdate u = EloEngine.update(1200, 1200, true, K);
    // delta = 32 * (1 - 0.5) = 16
    assertThat(u.delta()).isCloseTo(16.0, within(1e-9));
    assertThat(u.learnerRating()).isCloseTo(1216.0, within(1e-9));
    assertThat(u.questionDifficulty()).isCloseTo(1184.0, within(1e-9));
  }

  @Test
  void wrongAnswerAtEvenMatchMovesByMinusSixteen() {
    EloUpdate u = EloEngine.update(1200, 1200, false, K);
    assertThat(u.delta()).isCloseTo(-16.0, within(1e-9));
    assertThat(u.learnerRating()).isCloseTo(1184.0, within(1e-9));
    assertThat(u.questionDifficulty()).isCloseTo(1216.0, within(1e-9));
  }

  @Test
  void upsetWinGivesLargeReward() {
    // Weak learner (1000) beats a hard question (1400): expected is low, so reward is large.
    double expected = EloEngine.expectedScore(1000, 1400);
    EloUpdate u = EloEngine.update(1000, 1400, true, K);
    assertThat(u.expectedScore()).isCloseTo(expected, within(1e-12));
    assertThat(u.delta()).isCloseTo(K * (1.0 - expected), within(1e-9));
    assertThat(u.delta()).isGreaterThan(K * 0.9);
  }

  @Test
  void learnerGainEqualsQuestionLoss() {
    EloUpdate u = EloEngine.update(1337, 1290, true, K);
    double learnerGain = u.learnerRating() - 1337;
    double questionLoss = 1290 - u.questionDifficulty();
    assertThat(learnerGain).isCloseTo(questionLoss, within(1e-9));
  }
}
