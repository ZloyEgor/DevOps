CREATE TABLE ordering (

    id           SERIAL             PRIMARY KEY,
    total_price  DOUBLE PRECISION,

    client_id    BIGINT,
    CONSTRAINT fk_client
      FOREIGN KEY (client_id)
          REFERENCES client (id),

    product_id   BIGINT,
    CONSTRAINT fk_product
      FOREIGN KEY (product_id)
          REFERENCES product (id)

);