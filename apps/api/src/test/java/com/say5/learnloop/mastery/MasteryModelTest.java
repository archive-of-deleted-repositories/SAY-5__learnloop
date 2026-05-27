package com.say5.learnloop.mastery;

import static org.assertj.core.api.Assertions.assertThat;

import com.say5.learnloop.domain.MasteryLevel;
import java.util.List;
import org.junit.jupiter.api.Test;

class MasteryModelTest {

  private static final List<Double> POOL = List.of(1000.0, 1100.0, 1200.0, 1300.0, 1400.0);

  @Test
  void scoreRisesWithRating() {
    double low = MasteryModel.score(900, POOL);
    double mid = MasteryModel.score(1200, POOL);
    double high = MasteryModel.score(1600, POOL);
    assertThat(low).isLessThan(mid);
    assertThat(mid).isLessThan(high);
  }

  @Test
  void midPoolRatingScoresAroundHalf() {
    // Rating equal to the pool mean (1200) yields a mean expected score near 0.5.
    assertThat(MasteryModel.score(1200, POOL)).isBetween(0.45, 0.55);
  }

  @Test
  void emptyPoolScoresZero() {
    assertThat(MasteryModel.score(1500, List.of())).isEqualTo(0.0);
  }

  @Test
  void bucketsCoverTheBands() {
    assertThat(MasteryModel.bucket(0.10)).isEqualTo(MasteryLevel.NOVICE);
    assertThat(MasteryModel.bucket(0.30)).isEqualTo(MasteryLevel.DEVELOPING);
    assertThat(MasteryModel.bucket(0.60)).isEqualTo(MasteryLevel.PROFICIENT);
    assertThat(MasteryModel.bucket(0.90)).isEqualTo(MasteryLevel.MASTERED);
  }

  @Test
  void harderPoolNeedsHigherRatingForSameBucket() {
    List<Double> easy = List.of(900.0, 1000.0, 1100.0);
    List<Double> hard = List.of(1500.0, 1600.0, 1700.0);
    MasteryLevel onEasy = MasteryModel.level(1200, easy);
    MasteryLevel onHard = MasteryModel.level(1200, hard);
    assertThat(onEasy.ordinal()).isGreaterThan(onHard.ordinal());
  }

  @Test
  void aHighRatedLearnerMastersTheSkill() {
    assertThat(MasteryModel.level(1900, POOL)).isEqualTo(MasteryLevel.MASTERED);
  }
}
