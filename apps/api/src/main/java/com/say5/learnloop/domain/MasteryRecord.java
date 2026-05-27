package com.say5.learnloop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/** Cached, derived mastery for a (learner, skill). */
@Entity
@Table(name = "mastery")
@IdClass(MasteryRecord.Key.class)
public class MasteryRecord {

  @Id
  @Column(name = "learner_id")
  private Long learnerId;

  @Id
  @Column(name = "skill_id")
  private Long skillId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MasteryLevel level;

  @Column(nullable = false)
  private double score;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected MasteryRecord() {}

  public MasteryRecord(
      Long learnerId, Long skillId, MasteryLevel level, double score, Instant updatedAt) {
    this.learnerId = learnerId;
    this.skillId = skillId;
    this.level = level;
    this.score = score;
    this.updatedAt = updatedAt;
  }

  public Long getLearnerId() {
    return learnerId;
  }

  public Long getSkillId() {
    return skillId;
  }

  public MasteryLevel getLevel() {
    return level;
  }

  public double getScore() {
    return score;
  }

  public void update(MasteryLevel level, double score, Instant updatedAt) {
    this.level = level;
    this.score = score;
    this.updatedAt = updatedAt;
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
