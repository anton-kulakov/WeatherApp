CREATE INDEX idx_users_login ON users(login);

CREATE INDEX idx_locations_user_id ON locations(user_id);

CREATE INDEX idx_sessions_expires_at ON sessions(expires_at);