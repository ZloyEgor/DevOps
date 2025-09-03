-- Create cart_item table for shopping cart functionality
CREATE TABLE IF NOT EXISTS cart_item (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_cart_item_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT uk_cart_item_client_product UNIQUE (client_id, product_id),
    CONSTRAINT chk_cart_item_quantity_positive CHECK (quantity > 0)
);

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_cart_item_client_id ON cart_item(client_id);
CREATE INDEX IF NOT EXISTS idx_cart_item_product_id ON cart_item(product_id);
