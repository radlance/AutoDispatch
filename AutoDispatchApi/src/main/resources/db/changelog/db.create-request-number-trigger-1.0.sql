CREATE OR REPLACE FUNCTION generate_unique_request_number()
    RETURNS TRIGGER AS $$
DECLARE
    new_number VARCHAR(8);
BEGIN
    LOOP
        new_number := substr(md5(random()::text), 1, 6);

        IF NOT EXISTS (SELECT 1 FROM request WHERE request_number = new_number) THEN
            NEW.request_number := new_number;
            RETURN NEW;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER request_number_trigger
    BEFORE INSERT ON request
    FOR EACH ROW
    WHEN (NEW.request_number IS NULL)
EXECUTE FUNCTION generate_unique_request_number();