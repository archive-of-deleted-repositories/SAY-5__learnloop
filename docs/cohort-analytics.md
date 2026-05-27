# Cohort analytics and weakest-skill attribution

The teacher view goes beyond a roster: it tells a teacher where the class as a
whole is struggling and which specific items are responsible.

## Skill ranking

For each skill the analytics computes two class-level signals over the students
in a class group:

- median mastery score (the median of each student's mastery score for the skill)
- failure rate (1 minus the class success rate across all answers to the skill)

These combine into a struggle score:

```
struggle = 0.5 * (1 - medianMastery) + 0.5 * failureRate
```

Skills are ranked worst-first by struggle score. A skill the whole class fails
and has low mastery in rises to the top.

## Drill-down

For each skill the report lists the questions with the lowest class success rate
(correct answers / attempts), capped at the three hardest. A question everyone
gets wrong shows a 0% success rate, flagging it as either genuinely hard or
mis-calibrated and worth a teacher's review.

## Endpoint

`GET /api/dashboards/cohort/{classGroup}` returns the ranked skills, each with
its median mastery, failure rate, struggle score, and hardest-question
drill-down.

## Proof

An integration test seeds a class that answers one skill entirely wrong and
another entirely right: the failing skill ranks worst (failure rate > 0.8) and
its hardest question surfaces at a 0% class success rate.
