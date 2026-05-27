package com.say5.learnloop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/** A learner's current Elo rating within one skill. */
@Entity
@Table(name = "learner_skill_ratings")
@IdClass(LearnerSkillRating.Key.class)
public class LearnerSkillRating {

  @Id
  @Column(name = "learner_id")
  private Long learnerId;

  @Id
  @Column(name = "skill_id")
  private Long skillId;

  @Column(nullable = false)
  private double rating;

  @Column(name = "answer_count", nullable = false)
  private int answerCount;

  protected LearnerSkillRating() {}

  public LearnerSkillRating(Long learnerId, Long skillId, double rating, int answerCount) {
    this.learnerId = learnerId;
    this.skillId = skillId;
    this.rating = rating;
    this.answerCount = answerCount;
  }

  public Long getLearnerId() {
    return learnerId;
  }

  public Long getSkillId() {
    return skillId;
  }

  public double getRating() {
    return rating;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }

  public int getAnswerCount() {
    return answerCount;
  }

  public void incrementAnswerCount() {
    this.answerCount++;
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
