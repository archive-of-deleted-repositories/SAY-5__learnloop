# Per-skill mastery model

Mastery is derived from the learner's current rating in a skill relative to the
difficulty spread of that skill's question pool. It is not stored as ground
truth; it is computed from the event-sourced response log and cached per
(learner, skill).

## Buckets

The learner rating maps to one of four buckets:

| Bucket      | Meaning                                                |
|-------------|--------------------------------------------------------|
| novice      | rating below the pool's easy band                      |
| developing  | rating within the lower-middle of the pool             |
| proficient  | rating above the pool median, below the hard band      |
| mastered    | rating at or above the pool's hard band                |

Exact band boundaries are derived from the pool's difficulty distribution so a
skill with harder questions requires a higher rating to reach the same bucket.
The decision table is pinned by tests in v1.

## Event sourcing

Every answer writes a `responses` row (immutable). Mastery is a fold over those
rows through the engine, then bucketed. The cached `mastery` row is a
materialized view of that fold, refreshed on each answer.
