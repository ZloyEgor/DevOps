CREATE TABLE IF NOT EXISTS catalog (
    id              SERIAL          PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    description     TEXT
);
