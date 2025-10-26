BEGIN;
ALTER TABLE request
    ADD COLUMN origin_id      INT,
    ADD COLUMN destination_id INT;

ALTER TABLE request
    ADD CONSTRAINT fk_request_origin FOREIGN KEY (origin_id) REFERENCES city (id),
    ADD CONSTRAINT fk_request_destination FOREIGN KEY (destination_id) REFERENCES city (id);

ALTER TABLE request
    DROP COLUMN IF EXISTS origin,
    DROP COLUMN IF EXISTS destination;
COMMIT;