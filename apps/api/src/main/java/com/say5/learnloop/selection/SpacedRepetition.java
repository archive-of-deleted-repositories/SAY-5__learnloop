package com.say5.learnloop.selection;

import java.time.Duration;
import java.time.Instant;

/**
 * SM-2-lite spaced-repetition scheduler. Tracks a repetition count, an ease factor and an interval
 * (in days) per (learner, skill). On a successful recall the interval grows; on a failure the
 * schedule resets so the item resurfaces soon. Pure functions over an immutable {@link Schedule}.
 */
public final class SpacedRepetition {

  static final double MIN_EASE = 1.3;
  static final double START_EASE = 2.5;
  static final long FIRST_INTERVAL_DAYS = 1;
  static final long SECOND_INTERVAL_DAYS = 6;

  private SpacedRepetition() {}

  /**
   * Immutable review state.
   *
   * @param repetitions consecutive successful recalls
   * @param easeFactor multiplier applied to the interval on success
   * @param intervalDays days until the item is due again
   * @param dueAt instant the item next becomes due
   */
  public record Schedule(int repetitions, double easeFactor, long intervalDays, Instant dueAt) {

    public static Schedule initial(Instant now) {
      // A freshly mastered item becomes due after the first interval.
      return new Schedule(
          0, START_EASE, FIRST_INTERVAL_DAYS, now.plus(Duration.ofDays(FIRST_INTERVAL_DAYS)));
    }

    public boolean isDue(Instant now) {
      return !now.isBefore(dueAt);
    }
  }

  /**
   * Apply a review outcome.
   *
   * @param schedule the prior schedule
   * @param recalled whether the learner recalled the item correctly
   * @param now the review time
   * @return the next schedule
   */
  public static Schedule review(Schedule schedule, boolean recalled, Instant now) {
    if (!recalled) {
      // Failure: reset repetitions, drop ease, resurface after the first interval.
      double ease = Math.max(MIN_EASE, schedule.easeFactor() - 0.20);
      return new Schedule(
          0, ease, FIRST_INTERVAL_DAYS, now.plus(Duration.ofDays(FIRST_INTERVAL_DAYS)));
    }

    int repetitions = schedule.repetitions() + 1;
    double ease = Math.max(MIN_EASE, schedule.easeFactor() + 0.10);
    long interval;
    if (repetitions == 1) {
      interval = FIRST_INTERVAL_DAYS;
    } else if (repetitions == 2) {
      interval = SECOND_INTERVAL_DAYS;
    } else {
      interval = Math.round(schedule.intervalDays() * ease);
    }
    return new Schedule(repetitions, ease, interval, now.plus(Duration.ofDays(interval)));
  }
}
