package com.say5.learnloop.bench;

/** Aggregate timings from a benchmark run. */
record BenchResult(
    long totalAnswers,
    long wallNanos,
    long engineNanos,
    long masteryNanos,
    long selectionNanos,
    long persistNanos) {

  double answersPerSecond() {
    return totalAnswers / (wallNanos / 1e9);
  }

  double engineMicrosPerAnswer() {
    return engineNanos / 1_000.0 / totalAnswers;
  }

  double masteryMicrosPerAnswer() {
    return masteryNanos / 1_000.0 / totalAnswers;
  }

  double selectionMicrosPerAnswer() {
    return selectionNanos / 1_000.0 / totalAnswers;
  }

  double persistMicrosPerAnswer() {
    return persistNanos / 1_000.0 / totalAnswers;
  }

  String toJson() {
    return String.format(
        "{%n"
            + "  \"totalAnswers\": %d,%n"
            + "  \"answersPerSecond\": %.1f,%n"
            + "  \"engineMicrosPerAnswer\": %.4f,%n"
            + "  \"masteryMicrosPerAnswer\": %.4f,%n"
            + "  \"selectionMicrosPerAnswer\": %.4f,%n"
            + "  \"persistMicrosPerAnswer\": %.4f%n"
            + "}%n",
        totalAnswers,
        answersPerSecond(),
        engineMicrosPerAnswer(),
        masteryMicrosPerAnswer(),
        selectionMicrosPerAnswer(),
        persistMicrosPerAnswer());
  }
}
