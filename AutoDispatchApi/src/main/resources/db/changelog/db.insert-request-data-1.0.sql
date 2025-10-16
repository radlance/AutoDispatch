INSERT INTO request (created_by_id, status_id, origin, destination, trip_purpose_id, trip_date, started_trip_at, ended_trip_at)
VALUES (4, 1, 'Москва', 'Санкт-Петербург', 1, '2025-10-20', null, null),
       (4, 2, 'Москва', 'Тула', 2, '2025-10-18', null, null),
       (4, 3, 'Москва', 'Калуга', 3, '2025-10-19', '2025-10-19 14:30:00', null),
       (4, 4, 'Москва', 'Рязань', 4, '2025-10-17', '2025-10-17 12:20:00', '2025-10-17 20:40:00'),
       (4, 1, 'Москва', 'Владимир', 1, '2025-10-21', null, null);
