# Question selection

Given a learner's per-skill rating, the selector chooses the next question whose
expected success is closest to the configured target (0.70, the
desirable-difficulty sweet spot: hard enough to learn from, easy enough to keep
going).

## Procedure

1. Filter the skill's pool to questions not seen in the last `cooldown` answers.
2. For each eligible question compute `expected` against the learner rating.
3. Pick the question minimizing `abs(expected - 0.70)`.

If every question is on cooldown, the cooldown is relaxed to the least-recently
seen item. The selector is pure given (rating, pool, recent-history).
