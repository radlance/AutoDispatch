UPDATE driver d
SET status_id = 2
WHERE EXISTS (
    SELECT 1
    FROM assignment a
             JOIN request r ON r.id = a.request_id
    WHERE a.driver_id = d.user_id
      AND r.started_trip_at IS NOT NULL
);
