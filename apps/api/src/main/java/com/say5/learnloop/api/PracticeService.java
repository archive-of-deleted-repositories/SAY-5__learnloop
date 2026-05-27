package com.say5.learnloop.api;

import com.say5.learnloop.api.Dtos.NextQuestionResponse;
import com.say5.learnloop.api.Dtos.QuestionView;
import com.say5.learnloop.api.Dtos.SubmitAnswerResponse;
import com.say5.learnloop.data.LearnerSkillRatingRepository;
import com.say5.learnloop.data.MasteryRepository;
import com.say5.learnloop.data.QuestionRepository;
import com.say5.learnloop.data.ResponseRepository;
import com.say5.learnloop.domain.LearnerSkillRating;
import com.say5.learnloop.domain.MasteryLevel;
import com.say5.learnloop.domain.MasteryRecord;
import com.say5.learnloop.domain.Question;
import com.say5.learnloop.domain.Response;
import com.say5.learnloop.engine.EloEngine;
import com.say5.learnloop.engine.EloUpdate;
import com.say5.learnloop.mastery.MasteryModel;
import com.say5.learnloop.selection.QuestionSelector;
import com.say5.learnloop.selection.QuestionSelector.Candidate;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Orchestrates an answer: engine update, persistence, mastery recompute, next-question pick. */
@Service
public class PracticeService {

  private final QuestionRepository questions;
  private final ResponseRepository responses;
  private final LearnerSkillRatingRepository ratings;
  private final MasteryRepository mastery;
  private final EngineProperties props;

  public PracticeService(
      QuestionRepository questions,
      ResponseRepository responses,
      LearnerSkillRatingRepository ratings,
      MasteryRepository mastery,
      EngineProperties props) {
    this.questions = questions;
    this.responses = responses;
    this.ratings = ratings;
    this.mastery = mastery;
    this.props = props;
  }

  @Transactional
  public SubmitAnswerResponse submit(
      long learnerId, long skillId, long questionId, boolean correct) {
    Question question =
        questions
            .findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("question " + questionId));

    LearnerSkillRating rating = ratingOf(learnerId, skillId);
    EloUpdate update =
        EloEngine.update(rating.getRating(), question.getDifficulty(), correct, props.kFactor());

    responses.save(
        new Response(
            learnerId,
            skillId,
            questionId,
            correct,
            rating.getRating(),
            update.learnerRating(),
            question.getDifficulty(),
            update.questionDifficulty(),
            Instant.now()));

    rating.setRating(update.learnerRating());
    rating.incrementAnswerCount();
    ratings.save(rating);

    question.setDifficulty(update.questionDifficulty());
    questions.save(question);

    MasteryRecord masteryRecord = recomputeMastery(learnerId, skillId, update.learnerRating());

    QuestionView next = pickNext(learnerId, skillId, update.learnerRating()).orElse(null);

    return new SubmitAnswerResponse(
        update.learnerRating(),
        update.questionDifficulty(),
        update.expectedScore(),
        masteryRecord.getLevel(),
        masteryRecord.getScore(),
        next);
  }

  @Transactional(readOnly = true)
  public NextQuestionResponse next(long learnerId, long skillId) {
    LearnerSkillRating rating = ratingOf(learnerId, skillId);
    QuestionView next = pickNext(learnerId, skillId, rating.getRating()).orElse(null);
    return new NextQuestionResponse(rating.getRating(), next);
  }

  private LearnerSkillRating ratingOf(long learnerId, long skillId) {
    return ratings
        .findById(new LearnerSkillRating.Key(learnerId, skillId))
        .orElseGet(() -> new LearnerSkillRating(learnerId, skillId, props.defaultRating(), 0));
  }

  MasteryRecord recomputeMastery(long learnerId, long skillId, double learnerRating) {
    List<Double> pool =
        questions.findBySkillId(skillId).stream().map(Question::getDifficulty).toList();
    double score = MasteryModel.score(learnerRating, pool);
    MasteryLevel level = MasteryModel.bucket(score);
    MasteryRecord record =
        mastery
            .findById(new MasteryRecord.Key(learnerId, skillId))
            .orElseGet(() -> new MasteryRecord(learnerId, skillId, level, score, Instant.now()));
    record.update(level, score, Instant.now());
    return mastery.save(record);
  }

  private Optional<QuestionView> pickNext(long learnerId, long skillId, double learnerRating) {
    List<Question> pool = questions.findBySkillId(skillId);
    List<Candidate> candidates =
        pool.stream().map(q -> new Candidate(q.getId(), q.getDifficulty())).toList();
    Set<Long> recent =
        new HashSet<>(
            responses.recentQuestionIds(learnerId, skillId, PageRequest.of(0, props.cooldown())));
    return QuestionSelector.select(learnerRating, candidates, recent, props.targetSuccess())
        .flatMap(c -> questions.findById(c.questionId()))
        .map(q -> new QuestionView(q.getId(), q.getPrompt(), q.getDifficulty()));
  }
}
