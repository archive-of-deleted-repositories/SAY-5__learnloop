# Spaced-repetition resurfacing

Once a learner reaches MASTERED in a skill, that skill is no longer drilled
continuously. Instead it is resurfaced on a spaced schedule so the system can
detect decay. The scheduler is an SM-2-lite variant.

## Schedule state

Per (learner, skill) the scheduler tracks:

- `repetitions`: consecutive successful recalls
- `easeFactor`: a multiplier (starts at 2.5, floored at 1.3)
- `intervalDays`: days until the item is due again
- `dueAt`: the instant it next becomes due

## Update rule

On a review outcome:

- Success: `repetitions` increments and the interval grows.
  - 1st success: 1 day
  - 2nd success: 6 days
  - later: `round(previousInterval * easeFactor)`, with ease nudged up by 0.10.
- Failure: `repetitions` resets to 0, ease drops by 0.20 (never below 1.3), and
  the interval resets to 1 day so the item resurfaces soon.

This is a pure function over an immutable `Schedule`, fully unit-tested:

- a mastered skill becomes due only after its interval elapses,
- a passed review lengthens the next interval,
- a failed review shortens it (back to the 1-day floor).

## Blending with new material

The selector blends due-for-review skills with fresh practice. When a learner
asks for the next item, any skill whose `dueAt` has passed is eligible for
resurfacing alongside new-material selection (`GET /api/practice/reviews/{learnerId}`
lists the skills currently due). New material still uses the 70%-target Elo
selection within a skill; spaced repetition decides *which* mastered skills to
revisit and *when*.
