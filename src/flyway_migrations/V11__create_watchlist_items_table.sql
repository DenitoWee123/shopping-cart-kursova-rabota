CREATE TABLE watchlist_items (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) REFERENCES app_user(id) ON DELETE CASCADE,
    product_id VARCHAR(255) REFERENCES product(id) ON DELETE CASCADE,
    target_price DECIMAL(10, 2),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);