package com.say5.learnloop.mastery;

import static org.assertj.core.api.Assertions.assertThat;

import com.say5.learnloop.domain.MasteryLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Decision table pinning the score-to-bucket bands. */
class MasteryDecisionTableTest {

  @DisplayName("score bands map to mastery buckets")
  @ParameterizedTest(name = "score {0} -> {1}")
  @CsvSource({
    "0.00, NOVICE",
    "0.10, NOVICE",
    "0.249, NOVICE",
    "0.25, DEVELOPING",
    "0.40, DEVELOPING",
    "0.499, DEVELOPING",
    "0.50, PROFICIENT",
    "0.70, PROFICIENT",
    "0.799, PROFICIENT",
    "0.80, MASTERED",
    "0.95, MASTERED",
    "1.00, MASTERED",
  })
  void bucketsAtBandBoundaries(double score, MasteryLevel expected) {
    assertThat(MasteryModel.bucket(score)).isEqualTo(expected);
  }
}
