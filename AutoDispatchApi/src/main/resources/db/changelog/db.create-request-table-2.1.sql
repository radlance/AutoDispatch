ALTER TABLE request
    ADD COLUMN request_number VARCHAR(6);

ALTER TABLE request
    ADD CONSTRAINT unique_request_number UNIQUE(request_number);