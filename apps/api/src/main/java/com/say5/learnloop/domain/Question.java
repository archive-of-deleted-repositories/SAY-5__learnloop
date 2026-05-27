package com.say5.learnloop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "questions")
public class Question {

  @Id private Long id;

  @Column(name = "skill_id", nullable = false)
  private Long skillId;

  @Column(nullable = false)
  private String prompt;

  @Column(name = "correct_choice", nullable = false)
  private String correctChoice;

  @Column(nullable = false)
  private double difficulty;

  protected Question() {}

  public Question(Long id, Long skillId, String prompt, String correctChoice, double difficulty) {
    this.id = id;
    this.skillId = skillId;
    this.prompt = prompt;
    this.correctChoice = correctChoice;
    this.difficulty = difficulty;
  }

  public Long getId() {
    return id;
  }

  public Long getSkillId() {
    return skillId;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getCorrectChoice() {
    return correctChoice;
  }

  public double getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(double difficulty) {
    this.difficulty = difficulty;
  }
}
