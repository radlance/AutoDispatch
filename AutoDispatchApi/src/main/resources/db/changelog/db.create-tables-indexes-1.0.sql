CREATE INDEX IF NOT EXISTS idx_request_customer_id ON request (customer_id);
CREATE INDEX IF NOT EXISTS idx_request_status_id ON request (status_id);
CREATE INDEX IF NOT EXISTS idx_request_cargo_type_id ON request (cargo_type_id);
CREATE INDEX IF NOT EXISTS idx_assignment_request_id ON assignment (request_id);
CREATE INDEX IF NOT EXISTS idx_assignment_driver_id ON assignment (driver_id);
CREATE INDEX IF NOT EXISTS idx_assignment_vehicle_id ON assignment (vehicle_id);