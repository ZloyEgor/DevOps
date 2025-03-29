CREATE TABLE IF NOT EXISTS product (
    id              SERIAL          PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    description     TEXT,
    price           NUMERIC(10, 2)  NOT NULL DEFAULT 0,
    catalog_id      INT
);

ALTER TABLE product
    ADD CONSTRAINT fk_catalog
        FOREIGN KEY (catalog_id)
            REFERENCES catalog (id)
            ON DELETE SET NULL;
