CREATE TABLE price_reports (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES app_user(id) ON DELETE CASCADE,
    product_id UUID REFERENCES product(id) ON DELETE CASCADE,
    store_id UUID REFERENCES store(id) ON DELETE CASCADE,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);