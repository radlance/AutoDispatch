INSERT INTO driver_shift (driver_id, day_of_week, start_time, end_time)
SELECT d.user_id, s.day_of_week, s.start_time::time, s.end_time::time
FROM driver d
         CROSS JOIN (
    VALUES (1, '09:00', '22:00'),
           (2, '09:00', '22:00'),
           (3, '09:00', '22:00'),
           (4, '09:00', '22:00'),
           (5, '09:00', '22:00')
    ) AS s(day_of_week, start_time, end_time)
ON CONFLICT ON CONSTRAINT uq_driver_shift DO NOTHING;

INSERT INTO driver_shift (driver_id, day_of_week, start_time, end_time)
SELECT d.user_id, 6, '10:00'::time, '14:00'::time
FROM driver d
WHERE d.user_id IN (1, 2, 5, 6, 7)
ON CONFLICT ON CONSTRAINT uq_driver_shift DO NOTHING;

INSERT INTO driver_shift (driver_id, day_of_week, start_time, end_time)
SELECT d.user_id, 6, '22:00'::time, '06:00'::time
FROM driver d
WHERE d.user_id IN (8, 9)
ON CONFLICT ON CONSTRAINT uq_driver_shift DO NOTHING;
