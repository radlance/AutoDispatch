CREATE TABLE driver
(
    user_id    INT PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    status_id  int REFERENCES driver_status (id),
    vehicle_id INT REFERENCES vehicle (id) UNIQUE
);