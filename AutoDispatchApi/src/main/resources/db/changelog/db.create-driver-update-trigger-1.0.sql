CREATE TRIGGER trg_driver_updated_at
    BEFORE UPDATE
    ON driver
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();