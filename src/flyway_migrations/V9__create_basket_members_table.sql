CREATE TABLE basket_member (
    id UUID PRIMARY KEY,
    basket_id UUID REFERENCES shopping_basket(id) ON DELETE CASCADE,
    user_id UUID REFERENCES app_user(id),
    role VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
