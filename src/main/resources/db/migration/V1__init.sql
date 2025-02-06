CREATE TABLE IF NOT EXISTS Users
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    login    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Locations
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    user_id    INT NOT NULL,
    latitude  DECIMAL(23, 20) NOT NULL,
    longitude DECIMAL(24, 20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    UNIQUE INDEX unique_latitude_longitude (user_id, latitude, longitude)
);

CREATE TABLE IF NOT EXISTS Sessions
(
    id        VARCHAR(255) PRIMARY KEY,
    user_id    INT NOT NULL,
    expires_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);




