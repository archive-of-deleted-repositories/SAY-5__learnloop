package com.say5.learnloop.bench;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Runs the answer-throughput benchmark. Full scale is 10k learners x 100 answers = 1M submissions.
 * Set {@code -Dbench.learners} / {@code -Dbench.answers} to override (CI uses a small smoke scale).
 * Results are written to {@code target/bench-result.json}.
 */
@Tag("bench")
class AnswerThroughputBench {

  @Test
  void measureThroughput() throws IOException {
    int learners = Integer.getInteger("bench.learners", 10_000);
    int answers = Integer.getInteger("bench.answers", 100);

    // Warmup to let the JIT settle before timing.
    BenchHarness.run(Math.min(learners, 200), answers);

    BenchResult result = BenchHarness.run(learners, answers);

    Path out = Path.of("target", "bench-result.json");
    Files.createDirectories(out.getParent());
    Files.writeString(out, result.toJson());

    System.out.println(result.toJson());
    System.out.printf(
        "BENCH answers/sec=%.0f engine=%.3fus mastery=%.3fus selection=%.3fus persist=%.3fus%n",
        result.answersPerSecond(),
        result.engineMicrosPerAnswer(),
        result.masteryMicrosPerAnswer(),
        result.selectionMicrosPerAnswer(),
        result.persistMicrosPerAnswer());

    assertThat(result.totalAnswers()).isEqualTo((long) learners * answers);
    assertThat(result.answersPerSecond()).isPositive();
  }
}
