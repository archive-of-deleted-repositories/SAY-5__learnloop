-- Spaced-repetition schedule per (learner, skill). A row exists once a skill is mastered.
CREATE TABLE review_schedule (
    learner_id    BIGINT NOT NULL REFERENCES learners (id),
    skill_id      BIGINT NOT NULL REFERENCES skills (id),
    repetitions   INTEGER NOT NULL DEFAULT 0,
    ease_factor   DOUBLE PRECISION NOT NULL DEFAULT 2.5,
    interval_days BIGINT NOT NULL DEFAULT 1,
    due_at        TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (learner_id, skill_id)
);

CREATE INDEX idx_review_due ON review_schedule (learner_id, due_at);
