package com.say5.learnloop.selection;

import static org.assertj.core.api.Assertions.assertThat;

import com.say5.learnloop.selection.SpacedRepetition.Schedule;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class SpacedRepetitionTest {

  private final Instant now = Instant.parse("2026-01-01T00:00:00Z");

  @Test
  void aMasteredSkillBecomesDueAfterItsInterval() {
    Schedule s = Schedule.initial(now);
    assertThat(s.isDue(now)).isFalse();
    assertThat(s.isDue(now.plus(Duration.ofDays(1)))).isTrue();
  }

  @Test
  void aPassedReviewLengthensTheNextInterval() {
    Schedule s = Schedule.initial(now);
    Schedule first = SpacedRepetition.review(s, true, now);
    Schedule second = SpacedRepetition.review(first, true, now);
    Schedule third = SpacedRepetition.review(second, true, now);
    assertThat(first.intervalDays()).isEqualTo(1);
    assertThat(second.intervalDays()).isEqualTo(6);
    assertThat(third.intervalDays()).isGreaterThan(second.intervalDays());
  }

  @Test
  void aFailedReviewShortensTheNextInterval() {
    Schedule s = Schedule.initial(now);
    Schedule grown =
        SpacedRepetition.review(
            SpacedRepetition.review(SpacedRepetition.review(s, true, now), true, now), true, now);
    assertThat(grown.intervalDays()).isGreaterThan(SpacedRepetition.FIRST_INTERVAL_DAYS);

    Schedule failed = SpacedRepetition.review(grown, false, now);
    assertThat(failed.intervalDays()).isEqualTo(SpacedRepetition.FIRST_INTERVAL_DAYS);
    assertThat(failed.intervalDays()).isLessThan(grown.intervalDays());
    assertThat(failed.repetitions()).isZero();
  }

  @Test
  void failureLowersEaseButNotBelowMinimum() {
    Schedule s = new Schedule(0, SpacedRepetition.MIN_EASE, 1, now);
    Schedule failed = SpacedRepetition.review(s, false, now);
    assertThat(failed.easeFactor()).isGreaterThanOrEqualTo(SpacedRepetition.MIN_EASE);
  }

  @Test
  void successGrowsEaseFactor() {
    Schedule s = Schedule.initial(now);
    Schedule next = SpacedRepetition.review(s, true, now);
    assertThat(next.easeFactor()).isGreaterThan(s.easeFactor());
  }
}
