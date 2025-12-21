CREATE OR REPLACE FUNCTION proc_update_driver_status(p_driver_id INT)
    RETURNS VOID AS
$$
DECLARE
    active_assignment_exists BOOLEAN;
BEGIN
    SELECT EXISTS (SELECT 1
                   FROM assignment
                   WHERE driver_id = p_driver_id
                     AND started_at IS NOT NULL
                     AND completed_at IS NULL)
    INTO active_assignment_exists;

    IF active_assignment_exists THEN
        UPDATE driver
        SET status_id = 2
        WHERE user_id = p_driver_id;
    ELSE
        UPDATE driver
        SET status_id = 1
        WHERE user_id = p_driver_id
          AND status_id = 2;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trg_assignment_changed()
    RETURNS TRIGGER AS
$$
BEGIN
    IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
        PERFORM proc_update_driver_status(NEW.driver_id);
    END IF;

    IF (TG_OP = 'DELETE' OR (TG_OP = 'UPDATE' AND NEW.driver_id != OLD.driver_id)) THEN
        PERFORM proc_update_driver_status(OLD.driver_id);
    END IF;

    IF (TG_OP = 'DELETE') THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER assignment_changes_trigger
    AFTER INSERT OR UPDATE OR DELETE
    ON assignment
    FOR EACH ROW
EXECUTE FUNCTION trg_assignment_changed();