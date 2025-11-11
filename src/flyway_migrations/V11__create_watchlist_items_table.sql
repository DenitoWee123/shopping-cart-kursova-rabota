CREATE TABLE watchlist_items (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES app_user(id) ON DELETE CASCADE,
    product_id UUID REFERENCES product(id) ON DELETE CASCADE,
    target_price DECIMAL(10, 2),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);