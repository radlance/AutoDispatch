CREATE OR REPLACE FUNCTION add_driver_if_role()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.role_id = 2 THEN
        INSERT INTO driver(user_id, status_id)
        VALUES (NEW.id, 1);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER on_user_insert_add_driver
    AFTER INSERT ON users
    FOR EACH ROW
EXECUTE FUNCTION add_driver_if_role();