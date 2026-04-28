CREATE TABLE refresh_token
(
    token      VARCHAR(255) PRIMARY KEY,
    user_id    INT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);