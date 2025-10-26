BEGIN;
ALTER TABLE users
    ADD COLUMN role_id      INT;

ALTER TABLE users
    ADD CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES role (id);

ALTER TABLE users
    DROP COLUMN IF EXISTS role,
    DROP COLUMN IF EXISTS is_active;
COMMIT;