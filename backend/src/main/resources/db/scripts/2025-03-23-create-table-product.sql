CREATE TABLE product (

     id           SERIAL                PRIMARY KEY,
     name         VARCHAR(255),
     description  TEXT,
     price        DOUBLE PRECISION,
     picture_url  VARCHAR(255),

     catalog_id   BIGINT,
     CONSTRAINT fk_catalog
         FOREIGN KEY (catalog_id)
             REFERENCES product_catalog (id)
);