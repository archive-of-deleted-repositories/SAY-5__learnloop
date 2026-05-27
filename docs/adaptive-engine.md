# Adaptive difficulty engine

Each learner holds a per-skill rating; each question holds a difficulty rating.
Both start at a configured default (1200) and move after every answer using a
standard Elo update.

## Expected score

For a learner rating `R` and a question difficulty `D`:

```
expected = 1 / (1 + 10^((D - R) / 400))
```

`expected` is the model's probability that the learner answers correctly. When
the learner and the question are evenly matched (`R == D`) the expected score is
exactly 0.5. A 400-point edge maps to roughly a 10:1 odds advantage.

## Rating update

After an answer with `actual` in {0, 1}:

```
deltaLearner = K * (actual - expected)
newLearner   = R + deltaLearner
newQuestion  = D - deltaLearner
```

The same magnitude that the learner gains, the question loses, and vice versa.
This zero-sum symmetry keeps the two rating pools commensurate over time. `K`
(default 32) sets how fast ratings move.

## Properties (enforced by tests in v1)

- `expected` is always in the open interval (0, 1).
- A correct answer never lowers the learner rating; an incorrect answer never
  raises it.
- The learner's gain equals the question's loss (symmetry).
- Ratings stay finite and bounded for any bounded input sequence.

The engine is a pure function with no I/O, so it is unit-tested against
hand-computed values and property-tested for the invariants above.
