INSERT INTO product_catalog (name, description, catalog_type) VALUES
    ('Spring Flowers', 'Various spring flowers and bouquets', 'FLOWERS'),
    ('Exotic Bouquets', 'Exotic species from tropical regions', 'FLOWERS'),
    ('Wedding Bouquets', 'Bouquets for weddings and ceremonies', 'FLOWERS'),
    ('Funeral Wreaths', 'Floral arrangements for memorial services', 'FLOWERS'),
    ('Houseplants', 'Indoor potted plants for home decoration', 'FLOWERS');

-- Plain text passwords for development
INSERT INTO client (email, username, password, user_role) VALUES
    ('alice@example.com', 'Alice', 'passAlice', 'CLIENT'),
    ('bob@example.com',   'Bob',   'passBob', 'CLIENT'),
    ('carol@example.com', 'Carol', 'passCarol', 'CLIENT'),
    ('david@example.com', 'David', 'passDavid', 'ADMIN'),
    ('eve@example.com',   'Eve',   'passEve', 'CLIENT');

INSERT INTO product (name, description, price, picture_url, catalog_id) VALUES
    ('Tulip Mix',     'Mixed tulip bouquet',         15.99, 'http://pics.example.com/tulip.jpg',    1),
    ('Spring Combo',  'Combination of daisies...',   19.99, 'http://pics.example.com/spring.jpg',   1),
    ('Orchid Exotic', 'Rare tropical orchids',       29.50, 'http://pics.example.com/orchid.jpg',   2),
    ('Rainforest Mix','Ferns and tropical leaves',   24.99, 'http://pics.example.com/rainforest.jpg', 2),
    ('Bride Bouquet', 'Classic white roses',         49.99, 'http://pics.example.com/bride.jpg',    3),
    ('Bridesmaid Set','Pink carnations',             39.99, 'http://pics.example.com/bridesmaid.jpg', 3),
    ('Wreath Dark',   'Dark-themed funeral wreath',  35.00, 'http://pics.example.com/wreath-dark.jpg', 4),
    ('Wreath Light',  'Light-themed funeral wreath', 32.50, 'http://pics.example.com/wreath-light.jpg',4),
    ('Ficus Plant',   'Indoor ficus tree',           27.99, 'http://pics.example.com/ficus.jpg',     5),
    ('Succulent Set', 'Small succulents',            12.99, 'http://pics.example.com/succulent.jpg', 5);

INSERT INTO ordering (total_price, client_id, product_id) VALUES
    (15.99,  1, 1),
    (29.50,  2, 3),
    (49.99,  3, 5),
    (12.99,  4, 10),
    (32.50,  5, 8);
