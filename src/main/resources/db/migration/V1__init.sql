CREATE TABLE IF NOT EXISTS Users
(
    ID       INT AUTO_INCREMENT PRIMARY KEY,
    Login    VARCHAR(255) NOT NULL,
    Password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Locations
(
    ID        INT AUTO_INCREMENT PRIMARY KEY,
    Name      VARCHAR(255) NOT NULL,
    UserID    INT NOT NULL,
    Latitude  DECIMAL(23, 20) NOT NULL,
    Longitude DECIMAL(24, 20) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(ID),
    UNIQUE INDEX unique_latitude_longitude (UserID, Latitude, Longitude)
);

CREATE TABLE IF NOT EXISTS Sessions
(
    ID        VARCHAR(255) PRIMARY KEY,
    UserID    INT NOT NULL,
    ExpiresAt DATETIME NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(ID)
);




