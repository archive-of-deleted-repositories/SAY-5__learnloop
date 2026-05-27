package com.say5.learnloop.bench;

import com.say5.learnloop.engine.AnswerPipeline;
import com.say5.learnloop.engine.EloUpdate;
import com.say5.learnloop.selection.QuestionSelector.Candidate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Drives a large number of answer-submissions through the adaptive pipeline and reports per-stage
 * timings. Pure compute plus an in-memory store that mimics the persistence the service performs
 * per answer (append a response, update a rating, recompute mastery, pick next).
 */
final class BenchHarness {

  private static final double K = 32.0;
  private static final double DEFAULT_RATING = 1200.0;
  private static final double TARGET = 0.70;
  private static final int COOLDOWN = 5;
  private static final int QUESTIONS_PER_SKILL = 60;

  private BenchHarness() {}

  static BenchResult run(int learners, int answersPerLearner) {
    Random random = new Random(42);

    List<Candidate> pool = new ArrayList<>(QUESTIONS_PER_SKILL);
    List<Double> poolDifficulties = new ArrayList<>(QUESTIONS_PER_SKILL);
    for (int i = 0; i < QUESTIONS_PER_SKILL; i++) {
      double difficulty = 900 + i * (600.0 / (QUESTIONS_PER_SKILL - 1));
      pool.add(new Candidate(i + 1, difficulty));
      poolDifficulties.add(difficulty);
    }

    long totalAnswers = 0;
    long engineNanos = 0;
    long masteryNanos = 0;
    long selectionNanos = 0;
    long persistNanos = 0;

    // Mutable in-memory store mimicking per-answer writes.
    double[] difficulties = new double[QUESTIONS_PER_SKILL];
    for (int i = 0; i < QUESTIONS_PER_SKILL; i++) {
      difficulties[i] = poolDifficulties.get(i);
    }

    long wallStart = System.nanoTime();
    for (int l = 0; l < learners; l++) {
      double rating = DEFAULT_RATING;
      Deque<Long> recentQueue = new ArrayDeque<>();
      Set<Long> recent = new HashSet<>();
      long currentQuestion = 1;
      List<long[]> responseLog = new ArrayList<>(answersPerLearner);

      for (int a = 0; a < answersPerLearner; a++) {
        int qIndex = (int) (currentQuestion - 1);
        double difficulty = difficulties[qIndex];
        boolean correct = random.nextDouble() < expected(rating, difficulty);

        long t0 = System.nanoTime();
        EloUpdate update = AnswerPipeline.applyAnswer(rating, difficulty, correct, K);
        long t1 = System.nanoTime();

        // Persist: append response, update rating + difficulty, update cooldown window.
        responseLog.add(new long[] {currentQuestion, correct ? 1 : 0});
        rating = update.learnerRating();
        difficulties[qIndex] = update.questionDifficulty();
        recentQueue.addLast(currentQuestion);
        recent.add(currentQuestion);
        if (recentQueue.size() > COOLDOWN) {
          recent.remove(recentQueue.removeFirst());
        }
        long t2 = System.nanoTime();

        AnswerPipeline.recomputeMastery(rating, poolDifficulties);
        long t3 = System.nanoTime();

        currentQuestion = AnswerPipeline.selectNext(rating, pool, recent, TARGET);
        long t4 = System.nanoTime();

        engineNanos += (t1 - t0);
        persistNanos += (t2 - t1);
        masteryNanos += (t3 - t2);
        selectionNanos += (t4 - t3);
        totalAnswers++;
      }
    }
    long wallNanos = System.nanoTime() - wallStart;

    return new BenchResult(
        totalAnswers, wallNanos, engineNanos, masteryNanos, selectionNanos, persistNanos);
  }

  private static double expected(double rating, double difficulty) {
    return 1.0 / (1.0 + Math.pow(10.0, (difficulty - rating) / 400.0));
  }
}
