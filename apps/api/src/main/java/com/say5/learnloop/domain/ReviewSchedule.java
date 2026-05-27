package com.say5.learnloop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/** Persisted SM-2-lite review schedule for a (learner, skill). */
@Entity
@Table(name = "review_schedule")
@IdClass(ReviewSchedule.Key.class)
public class ReviewSchedule {

  @Id
  @Column(name = "learner_id")
  private Long learnerId;

  @Id
  @Column(name = "skill_id")
  private Long skillId;

  @Column(nullable = false)
  private int repetitions;

  @Column(name = "ease_factor", nullable = false)
  private double easeFactor;

  @Column(name = "interval_days", nullable = false)
  private long intervalDays;

  @Column(name = "due_at", nullable = false)
  private Instant dueAt;

  protected ReviewSchedule() {}

  public ReviewSchedule(
      Long learnerId,
      Long skillId,
      int repetitions,
      double easeFactor,
      long intervalDays,
      Instant dueAt) {
    this.learnerId = learnerId;
    this.skillId = skillId;
    this.repetitions = repetitions;
    this.easeFactor = easeFactor;
    this.intervalDays = intervalDays;
    this.dueAt = dueAt;
  }

  public Long getLearnerId() {
    return learnerId;
  }

  public Long getSkillId() {
    return skillId;
  }

  public int getRepetitions() {
    return repetitions;
  }

  public double getEaseFactor() {
    return easeFactor;
  }

  public long getIntervalDays() {
    return intervalDays;
  }

  public Instant getDueAt() {
    return dueAt;
  }

  public void apply(int repetitions, double easeFactor, long intervalDays, Instant dueAt) {
    this.repetitions = repetitions;
    this.easeFactor = easeFactor;
    this.intervalDays = intervalDays;
    this.dueAt = dueAt;
  }

  /** Composite primary key. */
  public static class Key implements Serializable {
    private Long learnerId;
    private Long skillId;

    public Key() {}

    public Key(Long learnerId, Long skillId) {
      this.learnerId = learnerId;
      this.skillId = skillId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Key key)) {
        return false;
      }
      return Objects.equals(learnerId, key.learnerId) && Objects.equals(skillId, key.skillId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(learnerId, skillId);
    }
  }
}
