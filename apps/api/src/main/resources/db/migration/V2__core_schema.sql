CREATE TABLE learners (
    id          BIGINT PRIMARY KEY,
    name        TEXT NOT NULL,
    role        TEXT NOT NULL CHECK (role IN ('STUDENT', 'TEACHER')),
    class_group TEXT
);

CREATE TABLE skills (
    id   BIGINT PRIMARY KEY,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL
);

CREATE TABLE questions (
    id             BIGINT PRIMARY KEY,
    skill_id       BIGINT NOT NULL REFERENCES skills (id),
    prompt         TEXT NOT NULL,
    correct_choice TEXT NOT NULL,
    difficulty     DOUBLE PRECISION NOT NULL
);

CREATE INDEX idx_questions_skill ON questions (skill_id);

CREATE TABLE learner_skill_ratings (
    learner_id   BIGINT NOT NULL REFERENCES learners (id),
    skill_id     BIGINT NOT NULL REFERENCES skills (id),
    rating       DOUBLE PRECISION NOT NULL,
    answer_count INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (learner_id, skill_id)
);

CREATE TABLE responses (
    id                         BIGSERIAL PRIMARY KEY,
    learner_id                 BIGINT NOT NULL REFERENCES learners (id),
    skill_id                   BIGINT NOT NULL REFERENCES skills (id),
    question_id                BIGINT NOT NULL REFERENCES questions (id),
    correct                    BOOLEAN NOT NULL,
    learner_rating_before      DOUBLE PRECISION NOT NULL,
    learner_rating_after       DOUBLE PRECISION NOT NULL,
    question_difficulty_before DOUBLE PRECISION NOT NULL,
    question_difficulty_after  DOUBLE PRECISION NOT NULL,
    answered_at                TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_responses_learner_skill ON responses (learner_id, skill_id, answered_at DESC);

CREATE TABLE mastery (
    learner_id BIGINT NOT NULL REFERENCES learners (id),
    skill_id   BIGINT NOT NULL REFERENCES skills (id),
    level      TEXT NOT NULL CHECK (level IN ('NOVICE', 'DEVELOPING', 'PROFICIENT', 'MASTERED')),
    score      DOUBLE PRECISION NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (learner_id, skill_id)
);
