-- Baseline marker migration. Domain schema lands in V2.
CREATE TABLE IF NOT EXISTS schema_baseline (
    id          SMALLINT PRIMARY KEY,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO schema_baseline (id) VALUES (1) ON CONFLICT DO NOTHING;
