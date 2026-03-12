ALTER TABLE vehicle
    ADD COLUMN region_code VARCHAR(3) NOT NULL DEFAULT '77';

UPDATE vehicle
SET region_code = '77'
WHERE region_code IS NULL;
