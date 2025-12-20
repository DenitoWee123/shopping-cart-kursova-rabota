ALTER TABLE app_user
    ADD COLUMN username VARCHAR(255),
    ADD COLUMN email VARCHAR(255),
    ADD COLUMN password_hash VARCHAR(255),
    ADD COLUMN location VARCHAR(255);

ALTER TABLE app_user
    DROP COLUMN name,
    DROP COLUMN age;