package com.say5.learnloop.api;

import com.say5.learnloop.domain.MasteryLevel;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request and response payloads for the practice and dashboard endpoints. */
public final class Dtos {

  private Dtos() {}

  public record SubmitAnswerRequest(
      @NotNull Long learnerId,
      @NotNull Long skillId,
      @NotNull Long questionId,
      @NotNull Boolean correct) {}

  public record QuestionView(Long questionId, String prompt, double difficulty) {}

  public record SubmitAnswerResponse(
      double learnerRating,
      double questionDifficulty,
      double expectedScore,
      MasteryLevel masteryLevel,
      double masteryScore,
      QuestionView nextQuestion) {}

  public record NextQuestionResponse(double learnerRating, QuestionView nextQuestion) {}

  public record SkillMasteryView(
      Long skillId,
      String skillCode,
      String skillName,
      double rating,
      MasteryLevel level,
      double score) {}

  public record TrendPoint(Long questionId, boolean correct, double learnerRatingAfter) {}

  public record StudentDashboard(
      Long learnerId, String name, List<SkillMasteryView> skills, List<TrendPoint> recentTrend) {}

  public record RosterEntry(Long learnerId, String name, List<SkillMasteryView> skills) {}

  public record SkillDistribution(
      Long skillId,
      String skillCode,
      String skillName,
      int novice,
      int developing,
      int proficient,
      int mastered,
      double medianScore) {}

  public record TeacherDashboard(
      String classGroup,
      List<RosterEntry> roster,
      List<SkillDistribution> distributions,
      List<SkillDistribution> weakestSkills) {}

  public record QuestionDifficultyView(
      Long questionId, String prompt, int attempts, int correct, double successRate) {}

  public record WeakSkillReport(
      Long skillId,
      String skillCode,
      String skillName,
      double medianMastery,
      double failureRate,
      double struggleScore,
      List<QuestionDifficultyView> hardestQuestions) {}

  public record CohortAnalytics(String classGroup, List<WeakSkillReport> rankedSkills) {}
}
