UPDATE driver
SET vehicle_id = 1
WHERE user_id = 1;

UPDATE driver
SET vehicle_id = 2
WHERE user_id = 2;

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