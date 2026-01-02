UPDATE driver
SET vehicle_id = v.new_vehicle_id
FROM (VALUES
          (1, 3),
          (2, 4),
          (5, 8),
          (6, 9),
          (7, 10),
          (8, 11),
          (9, 12),
          (10, 13),
          (11, 14),
          (13, 16),
          (12, 15),
          (14, 17),
          (15, 18),
          (16, 19)
     ) AS v(target_user_id, new_vehicle_id)
WHERE driver.user_id = v.target_user_id;

-- UPDATE driver
-- SET vehicle_id = 3
-- WHERE user_id = 4;

UPDATE driver
SET status_id = (SELECT ds.id
                 FROM driver_status ds
                 WHERE ds.name = 'В рейсе')
WHERE user_id IN (SELECT a.driver_id
                  FROM assignment a
                           JOIN request r ON r.id = a.request_id
                           JOIN request_status rs ON rs.id = r.status_id
                  WHERE rs.name = 'В пути');