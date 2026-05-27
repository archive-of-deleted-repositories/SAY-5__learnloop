# LearnLoop

Adaptive practice and progress-tracking web app. Learners answer questions and an
Elo-style engine adapts the next question's difficulty to their recent performance.
Per-skill mastery is tracked in SQL and surfaced on student and teacher dashboards.

## What makes it different

The load-bearing piece is the adaptive-difficulty engine plus a per-skill mastery
model that updates from every answer:

- Each learner has a per-skill rating; each question has a difficulty rating.
- On each answer both ratings move by an Elo update: `K * (actual - expected)`,
  where `expected = 1 / (1 + 10^((q_diff - learner_rating) / 400))`.
- The selector picks the next question whose expected success is closest to 70%
  (the desirable-difficulty sweet spot), avoiding recently-seen items (cooldown).
- Mastery is a smoothed function of the learner's rating versus the skill's question
  pool, bucketed into novice / developing / proficient / mastered, derived from an
  event-sourced log of responses.

See `docs/` for the engine, mastery model, selection, and dashboard designs.

## Stack

- Backend: Java 21, Spring Boot 3, Spring Web + Data JPA, Postgres, Flyway
- Frontend: React 18 + TypeScript + Vite + TanStack Query + Recharts
- Tests: JUnit 5 + AssertJ + Testcontainers (api), Vitest + RTL (web), Playwright (e2e)
- Packaging: multi-stage Dockerfiles, docker-compose for local + e2e

## Layout

```
apps/api    Spring Boot service (engine, mastery, selection, domain, api, data)
apps/web    React + TypeScript SPA
db          Flyway migrations and seed data
docs        design notes
```

## Quick start

```sh
make up         # postgres + api + web via docker-compose
make test       # backend unit + integration tests (testcontainers)
make test-web   # frontend unit tests
make e2e        # Playwright end-to-end against docker-compose
```

Backend requires Java 21 and a running Docker daemon (for Testcontainers).

## License

MIT. See `LICENSE`.
