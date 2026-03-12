ALTER TABLE request
    ADD COLUMN arrived_loading_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN arrived_unloading_at TIMESTAMP WITH TIME ZONE;
