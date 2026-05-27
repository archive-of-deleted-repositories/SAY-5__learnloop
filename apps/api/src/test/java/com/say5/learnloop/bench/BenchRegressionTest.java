package com.say5.learnloop.bench;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Guards against answer-throughput regressions. Compares the measured per-answer engine cost
 * against a committed baseline ({@code bench-baseline.json} on the test classpath). Fails if the
 * cost grows by more than the allowed margin (30%). The baseline is regenerated intentionally when
 * the pipeline changes.
 */
@Tag("bench")
class BenchRegressionTest {

  private static final double MAX_REGRESSION = 0.30;

  @Test
  void engineCostHasNotRegressed() throws IOException {
    int learners = Integer.getInteger("bench.learners", 2_000);
    int answers = Integer.getInteger("bench.answers", 50);

    BenchHarness.run(200, answers); // warmup
    BenchResult result = BenchHarness.run(learners, answers);

    double baselineEngine = baseline("engineMicrosPerAnswer");
    double baselineMastery = baseline("masteryMicrosPerAnswer");
    double baselineSelection = baseline("selectionMicrosPerAnswer");

    System.out.printf(
        "REGRESS engine=%.3f/%.3f mastery=%.3f/%.3f selection=%.3f/%.3f (measured/baseline us)%n",
        result.engineMicrosPerAnswer(),
        baselineEngine,
        result.masteryMicrosPerAnswer(),
        baselineMastery,
        result.selectionMicrosPerAnswer(),
        baselineSelection);

    assertWithinMargin("engine", result.engineMicrosPerAnswer(), baselineEngine);
    assertWithinMargin("mastery", result.masteryMicrosPerAnswer(), baselineMastery);
    assertWithinMargin("selection", result.selectionMicrosPerAnswer(), baselineSelection);
  }

  private static void assertWithinMargin(String stage, double measured, double baseline) {
    double limit = baseline * (1.0 + MAX_REGRESSION);
    assertThat(measured)
        .as(
            "%s cost %.3fus must stay within %.0f%% of baseline %.3fus",
            stage, measured, MAX_REGRESSION * 100, baseline)
        .isLessThanOrEqualTo(limit);
  }

  private static double baseline(String key) throws IOException {
    try (InputStream in = BenchRegressionTest.class.getResourceAsStream("/bench-baseline.json")) {
      if (in == null) {
        throw new IllegalStateException("bench-baseline.json missing from classpath");
      }
      String json = new String(in.readAllBytes());
      Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9.]+)").matcher(json);
      if (!m.find()) {
        throw new IllegalStateException("key " + key + " missing from baseline");
      }
      return Double.parseDouble(m.group(1));
    }
  }
}
