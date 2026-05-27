package com.say5.learnloop.api;

import com.say5.learnloop.api.Dtos.RosterEntry;
import com.say5.learnloop.api.Dtos.SkillDistribution;
import com.say5.learnloop.api.Dtos.SkillMasteryView;
import com.say5.learnloop.api.Dtos.StudentDashboard;
import com.say5.learnloop.api.Dtos.TeacherDashboard;
import com.say5.learnloop.api.Dtos.TrendPoint;
import com.say5.learnloop.data.LearnerRepository;
import com.say5.learnloop.data.LearnerSkillRatingRepository;
import com.say5.learnloop.data.MasteryRepository;
import com.say5.learnloop.data.ResponseRepository;
import com.say5.learnloop.data.SkillRepository;
import com.say5.learnloop.domain.Learner;
import com.say5.learnloop.domain.LearnerSkillRating;
import com.say5.learnloop.domain.MasteryLevel;
import com.say5.learnloop.domain.MasteryRecord;
import com.say5.learnloop.domain.Skill;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read models for the student and teacher dashboards. */
@Service
@Transactional(readOnly = true)
public class DashboardService {

  private static final int TREND_SIZE = 10;

  private final LearnerRepository learners;
  private final SkillRepository skills;
  private final LearnerSkillRatingRepository ratings;
  private final MasteryRepository mastery;
  private final ResponseRepository responses;

  public DashboardService(
      LearnerRepository learners,
      SkillRepository skills,
      LearnerSkillRatingRepository ratings,
      MasteryRepository mastery,
      ResponseRepository responses) {
    this.learners = learners;
    this.skills = skills;
    this.ratings = ratings;
    this.mastery = mastery;
    this.responses = responses;
  }

  public StudentDashboard student(long learnerId) {
    Learner learner =
        learners
            .findById(learnerId)
            .orElseThrow(() -> new EntityNotFoundException("learner " + learnerId));
    Map<Long, Skill> skillsById =
        skills.findAll().stream().collect(Collectors.toMap(Skill::getId, Function.identity()));

    List<SkillMasteryView> views = skillViews(learnerId, skillsById);

    long primarySkill = views.isEmpty() ? 0L : views.get(0).skillId();
    List<TrendPoint> trend =
        responses
            .findByLearnerIdAndSkillIdOrderByAnsweredAtAscIdAsc(learnerId, primarySkill)
            .stream()
            .map(r -> new TrendPoint(r.getQuestionId(), r.isCorrect(), r.getLearnerRatingAfter()))
            .toList();
    if (trend.size() > TREND_SIZE) {
      trend = trend.subList(trend.size() - TREND_SIZE, trend.size());
    }

    return new StudentDashboard(learnerId, learner.getName(), views, trend);
  }

  private List<SkillMasteryView> skillViews(long learnerId, Map<Long, Skill> skillsById) {
    Map<Long, LearnerSkillRating> ratingBySkill =
        ratings.findByLearnerId(learnerId).stream()
            .collect(Collectors.toMap(LearnerSkillRating::getSkillId, Function.identity()));
    Map<Long, MasteryRecord> masteryBySkill =
        mastery.findByLearnerId(learnerId).stream()
            .collect(Collectors.toMap(MasteryRecord::getSkillId, Function.identity()));

    return skillsById.values().stream()
        .sorted(Comparator.comparing(Skill::getId))
        .map(
            skill -> {
              LearnerSkillRating r = ratingBySkill.get(skill.getId());
              MasteryRecord m = masteryBySkill.get(skill.getId());
              double rating = r != null ? r.getRating() : 0.0;
              MasteryLevel level = m != null ? m.getLevel() : MasteryLevel.NOVICE;
              double score = m != null ? m.getScore() : 0.0;
              return new SkillMasteryView(
                  skill.getId(), skill.getCode(), skill.getName(), rating, level, score);
            })
        .toList();
  }

  public TeacherDashboard teacher(String classGroup) {
    List<Learner> roster =
        learners.findByClassGroup(classGroup).stream()
            .filter(l -> l.getRole() == com.say5.learnloop.domain.Role.STUDENT)
            .sorted(Comparator.comparing(Learner::getId))
            .toList();
    Map<Long, Skill> skillsById =
        skills.findAll().stream().collect(Collectors.toMap(Skill::getId, Function.identity()));

    List<RosterEntry> entries =
        roster.stream()
            .map(l -> new RosterEntry(l.getId(), l.getName(), skillViews(l.getId(), skillsById)))
            .toList();

    List<SkillDistribution> distributions = distributions(entries, skillsById);
    List<SkillDistribution> weakest =
        distributions.stream()
            .sorted(Comparator.comparingDouble(SkillDistribution::medianScore))
            .toList();

    return new TeacherDashboard(classGroup, entries, distributions, weakest);
  }

  List<SkillDistribution> distributions(List<RosterEntry> roster, Map<Long, Skill> skillsById) {
    List<SkillDistribution> out = new ArrayList<>();
    for (Skill skill :
        skillsById.values().stream().sorted(Comparator.comparing(Skill::getId)).toList()) {
      int novice = 0;
      int developing = 0;
      int proficient = 0;
      int mastered = 0;
      List<Double> scores = new ArrayList<>();
      for (RosterEntry entry : roster) {
        SkillMasteryView view =
            entry.skills().stream()
                .filter(s -> s.skillId().equals(skill.getId()))
                .findFirst()
                .orElse(null);
        if (view == null) {
          continue;
        }
        scores.add(view.score());
        switch (view.level()) {
          case NOVICE -> novice++;
          case DEVELOPING -> developing++;
          case PROFICIENT -> proficient++;
          case MASTERED -> mastered++;
        }
      }
      out.add(
          new SkillDistribution(
              skill.getId(),
              skill.getCode(),
              skill.getName(),
              novice,
              developing,
              proficient,
              mastered,
              median(scores)));
    }
    return out;
  }

  static double median(List<Double> values) {
    if (values.isEmpty()) {
      return 0.0;
    }
    List<Double> sorted = values.stream().sorted().toList();
    int n = sorted.size();
    if (n % 2 == 1) {
      return sorted.get(n / 2);
    }
    return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
  }
}
