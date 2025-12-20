CREATE TABLE shopping_cart.fantastiko_entity (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL
);
