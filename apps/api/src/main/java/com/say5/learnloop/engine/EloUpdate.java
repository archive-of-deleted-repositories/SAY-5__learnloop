package com.say5.learnloop.engine;

/**
 * Result of one Elo update.
 *
 * @param learnerRating new learner rating
 * @param questionDifficulty new question difficulty
 * @param expectedScore the expected score used for this update, in (0, 1)
 * @param delta the signed rating change applied to the learner (the question moved by -delta)
 */
public record EloUpdate(
    double learnerRating, double questionDifficulty, double expectedScore, double delta) {}
