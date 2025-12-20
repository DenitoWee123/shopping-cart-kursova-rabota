CREATE TABLE basket_items (
    id UUID PRIMARY KEY,
    basket_id UUID REFERENCES shopping_basket(id) ON DELETE CASCADE,
    product_id UUID REFERENCES product(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1,
    added_by UUID REFERENCES app_user(id),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);