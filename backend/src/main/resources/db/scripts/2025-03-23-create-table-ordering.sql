CREATE TABLE IF NOT EXISTS ordering (
    id              SERIAL          PRIMARY KEY,
    client_id       INT             NOT NULL,
    order_date      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_price     NUMERIC(10, 2)
);

ALTER TABLE ordering
    ADD CONSTRAINT fk_client
        FOREIGN KEY (client_id)
            REFERENCES client (id)
            ON DELETE CASCADE;
