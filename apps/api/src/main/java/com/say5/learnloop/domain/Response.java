package com.say5.learnloop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Immutable event: one answer by a learner to a question. */
@Entity
@Table(name = "responses")
public class Response {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "learner_id", nullable = false)
  private Long learnerId;

  @Column(name = "skill_id", nullable = false)
  private Long skillId;

  @Column(name = "question_id", nullable = false)
  private Long questionId;

  @Column(nullable = false)
  private boolean correct;

  @Column(name = "learner_rating_before", nullable = false)
  private double learnerRatingBefore;

  @Column(name = "learner_rating_after", nullable = false)
  private double learnerRatingAfter;

  @Column(name = "question_difficulty_before", nullable = false)
  private double questionDifficultyBefore;

  @Column(name = "question_difficulty_after", nullable = false)
  private double questionDifficultyAfter;

  @Column(name = "answered_at", nullable = false)
  private Instant answeredAt;

  protected Response() {}

  public Response(
      Long learnerId,
      Long skillId,
      Long questionId,
      boolean correct,
      double learnerRatingBefore,
      double learnerRatingAfter,
      double questionDifficultyBefore,
      double questionDifficultyAfter,
      Instant answeredAt) {
    this.learnerId = learnerId;
    this.skillId = skillId;
    this.questionId = questionId;
    this.correct = correct;
    this.learnerRatingBefore = learnerRatingBefore;
    this.learnerRatingAfter = learnerRatingAfter;
    this.questionDifficultyBefore = questionDifficultyBefore;
    this.questionDifficultyAfter = questionDifficultyAfter;
    this.answeredAt = answeredAt;
  }

  public Long getId() {
    return id;
  }

  public Long getLearnerId() {
    return learnerId;
  }

  public Long getSkillId() {
    return skillId;
  }

  public Long getQuestionId() {
    return questionId;
  }

  public boolean isCorrect() {
    return correct;
  }

  public double getLearnerRatingAfter() {
    return learnerRatingAfter;
  }

  public double getQuestionDifficultyAfter() {
    return questionDifficultyAfter;
  }

  public Instant getAnsweredAt() {
    return answeredAt;
  }
}
