package com.say5.learnloop.api;

import com.say5.learnloop.api.Dtos.CohortAnalytics;
import com.say5.learnloop.api.Dtos.QuestionDifficultyView;
import com.say5.learnloop.api.Dtos.WeakSkillReport;
import com.say5.learnloop.data.LearnerRepository;
import com.say5.learnloop.data.MasteryRepository;
import com.say5.learnloop.data.QuestionRepository;
import com.say5.learnloop.data.ResponseRepository;
import com.say5.learnloop.data.SkillRepository;
import com.say5.learnloop.domain.Learner;
import com.say5.learnloop.domain.MasteryRecord;
import com.say5.learnloop.domain.Question;
import com.say5.learnloop.domain.Role;
import com.say5.learnloop.domain.Skill;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class-level analytics for teachers. Ranks skills by how much a class struggles (low median
 * mastery and high failure rate) and, for each skill, surfaces the questions with the lowest class
 * success rate (candidate mis-calibrated or genuinely hard items).
 */
@Service
@Transactional(readOnly = true)
public class CohortAnalyticsService {

  private static final int DRILL_DOWN_LIMIT = 3;

  private final LearnerRepository learners;
  private final SkillRepository skills;
  private final MasteryRepository mastery;
  private final ResponseRepository responses;
  private final QuestionRepository questions;

  public CohortAnalyticsService(
      LearnerRepository learners,
      SkillRepository skills,
      MasteryRepository mastery,
      ResponseRepository responses,
      QuestionRepository questions) {
    this.learners = learners;
    this.skills = skills;
    this.mastery = mastery;
    this.responses = responses;
    this.questions = questions;
  }

  public CohortAnalytics forClass(String classGroup) {
    List<Long> studentIds =
        learners.findByClassGroup(classGroup).stream()
            .filter(l -> l.getRole() == Role.STUDENT)
            .map(Learner::getId)
            .toList();

    List<WeakSkillReport> reports = new ArrayList<>();
    for (Skill skill : skills.findAll()) {
      reports.add(report(skill, studentIds));
    }

    // Rank worst-first: higher struggle score = more trouble.
    reports.sort(Comparator.comparingDouble(WeakSkillReport::struggleScore).reversed());
    return new CohortAnalytics(classGroup, reports);
  }

  private WeakSkillReport report(Skill skill, List<Long> studentIds) {
    double medianMastery = medianMastery(skill.getId(), studentIds);
    double failureRate = failureRate(skill.getId(), studentIds);
    // Struggle: weighted blend of low mastery and high failure, both in [0, 1].
    double struggle = 0.5 * (1.0 - medianMastery) + 0.5 * failureRate;

    return new WeakSkillReport(
        skill.getId(),
        skill.getCode(),
        skill.getName(),
        medianMastery,
        failureRate,
        struggle,
        hardestQuestions(skill.getId(), studentIds));
  }

  private double medianMastery(Long skillId, List<Long> studentIds) {
    if (studentIds.isEmpty()) {
      return 0.0;
    }
    Map<Long, Double> byLearner =
        mastery.findBySkillId(skillId).stream()
            .filter(m -> studentIds.contains(m.getLearnerId()))
            .collect(Collectors.toMap(MasteryRecord::getLearnerId, MasteryRecord::getScore));
    List<Double> scores =
        studentIds.stream().map(id -> byLearner.getOrDefault(id, 0.0)).sorted().toList();
    int n = scores.size();
    if (n % 2 == 1) {
      return scores.get(n / 2);
    }
    return (scores.get(n / 2 - 1) + scores.get(n / 2)) / 2.0;
  }

  private double failureRate(Long skillId, List<Long> studentIds) {
    if (studentIds.isEmpty()) {
      return 0.0;
    }
    List<Object[]> rows = responses.skillSuccess(skillId, studentIds);
    if (rows.isEmpty()) {
      return 0.0;
    }
    Object[] row = rows.get(0);
    long correct = row[0] == null ? 0L : ((Number) row[0]).longValue();
    long total = row[1] == null ? 0L : ((Number) row[1]).longValue();
    if (total == 0) {
      return 0.0;
    }
    return 1.0 - (double) correct / total;
  }

  private List<QuestionDifficultyView> hardestQuestions(Long skillId, List<Long> studentIds) {
    if (studentIds.isEmpty()) {
      return List.of();
    }
    Map<Long, Question> questionsById =
        questions.findBySkillId(skillId).stream()
            .collect(Collectors.toMap(Question::getId, Function.identity()));

    List<QuestionDifficultyView> views = new ArrayList<>();
    for (Object[] row : responses.questionSuccessCounts(skillId, studentIds)) {
      long questionId = ((Number) row[0]).longValue();
      long correct = ((Number) row[1]).longValue();
      long total = ((Number) row[2]).longValue();
      double rate = total == 0 ? 0.0 : (double) correct / total;
      Question q = questionsById.get(questionId);
      String prompt = q != null ? q.getPrompt() : "question " + questionId;
      views.add(new QuestionDifficultyView(questionId, prompt, (int) total, (int) correct, rate));
    }

    views.sort(Comparator.comparingDouble(QuestionDifficultyView::successRate));
    return views.stream().limit(DRILL_DOWN_LIMIT).toList();
  }
}
