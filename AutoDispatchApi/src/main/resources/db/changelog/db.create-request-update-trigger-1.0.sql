CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_request_updated_at
    BEFORE UPDATE
    ON request
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();