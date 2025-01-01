CREATE TABLE IF NOT EXISTS health_check (
    id SERIAL PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO health_check(message) VALUES ('postgres ok');