CREATE TRIGGER trg_request_updated_at
    BEFORE UPDATE
    ON request
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();